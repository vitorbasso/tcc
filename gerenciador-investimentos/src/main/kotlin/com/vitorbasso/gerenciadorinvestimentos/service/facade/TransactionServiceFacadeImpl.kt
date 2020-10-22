package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
internal class TransactionServiceFacadeImpl(
    private val transactionService: TransactionService,
    private val stockService: StockService,
    private val assetService: AssetService,
    private val walletService: WalletService
) : ITransactionService {

    @Transactional
    override fun performTransaction(transactionRequest: TransactionRequest) = when (transactionRequest.type) {
        TransactionType.BUY -> processTransaction(
            transactionRequest = transactionRequest,
            wallet = this.walletService.getWallet(SecurityContextUtil.getClientDetails(), transactionRequest.broker),
            updateAsset = this.assetService::addToAsset
        )
        TransactionType.SELL -> processTransaction(
            transactionRequest = transactionRequest,
            wallet = this.walletService.getWallet(SecurityContextUtil.getClientDetails(), transactionRequest.broker),
            updateAsset = this.assetService::subtractFromAsset
        )
    }

    private fun processTransaction(
        transactionRequest: TransactionRequest,
        wallet: Wallet,
        updateAsset: (
            wallet: Wallet,
            stock: Stock,
            quantity: Int,
            value: BigDecimal
        ) -> Asset
    ) = updateAsset(
        wallet,
        this.stockService.getStock(transactionRequest.ticker),
        transactionRequest.quantity,
        transactionRequest.value
    ).let {
        Transaction(
            type = transactionRequest.type,
            quantity = transactionRequest.quantity,
            value = transactionRequest.value,
            asset = it,
            transactionDate = transactionRequest.date
        )
    }.let {
        this.transactionService.save(calculateDaytrade(it))
    }

    private fun calculateDaytrade(transaction: Transaction) : Transaction {
        val transactions = this.transactionService.findTransactionsOnDate(transaction.asset, transaction.transactionDate)
        var sameTypeQuantity = transactions.filter { it.type == transaction.type }.fold(0) { total, tx -> total + tx.quantity} * -1
        val otherTypeTransactionList = transactions.filter { it.type != transaction.type }

        var quantityNotDaytrade = 0

        for(otherTypeTransaction in otherTypeTransactionList){
            sameTypeQuantity += otherTypeTransaction.quantity
            if(sameTypeQuantity > 0) {
                quantityNotDaytrade = updatePastTransactions(otherTypeTransactionList.subList(otherTypeTransactionList.indexOf(otherTypeTransaction), otherTypeTransactionList.size), sameTypeQuantity, transaction.quantity)
                break
            }
        }

        return if(sameTypeQuantity > 0) transaction.copy(daytrade = true, daytradeQuantity = transaction.quantity - quantityNotDaytrade)
        else transaction
    }

    private fun updatePastTransactions(otherTypeTransactions: List<Transaction>, rest: Int, transactionQuantity: Int) : Int {
        var left = transactionQuantity

        fun updateTransaction(transaction: Transaction) {
            if (left > transaction.quantity) {
                this.transactionService.save(transaction.copy(daytrade = true, daytradeQuantity = transaction.quantity))
                left -= transaction.quantity
            } else {
                this.transactionService.save(transaction.copy(daytrade = true, daytradeQuantity = left))
                left = 0
            }
        }

        otherTypeTransactions.forEachIndexed {index, transaction ->
            if(left > 0){
                if (index > 0) {
                    updateTransaction(transaction)
                } else {
                    if(rest > 0) {
                        if (rest < left) {
                            this.transactionService.save(transaction.copy(daytrade = true, daytradeQuantity = transaction.quantity))
                            left -= rest
                        } else {
                            this.transactionService.save(transaction.copy(daytrade = true, daytradeQuantity = transaction.quantity - (rest - left)))
                            left = 0
                        }
                    } else {
                        updateTransaction(transaction)
                    }
                }
            }
        }

        return left
    }

//    private fun isDaytrade(asset: Asset, transaction: Transaction)
//        = this.transactionService.findTransactionsOnDate(asset, transaction.transactionDate).let {transactionsOnDate ->
//        transactionsOnDate.any { it.type != transaction.type }
//            && quantityChecksOut(transactionsOnDate, transaction.type)
//    }
//
//    private fun quantityChecksOut(transactionsOnDate: List<Transaction>, transactionType: TransactionType)
//        = transactionsOnDate.fold(0) { total, transaction ->
//        when (transaction.type) {
//            TransactionType.BUY -> total + transaction.quantity
//            TransactionType.SELL -> total - transaction.quantity
//        }
//    }.let {
//        when (transactionType) {
//            TransactionType.SELL -> it > 0
//            TransactionType.BUY -> it < 0
//        }
//    }

}