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
        this.transactionService.save(processDaytrade(it))
    }

    private fun processDaytrade(transaction: Transaction): Transaction {
        val sameDayTransactions = this.transactionService.findTransactionsOnDate(
            transaction.asset,
            transaction.transactionDate
        )

        var sameTypeTransactionAssetQuantity = -1 * sameDayTransactions.filter {
            it.type == transaction.type && it.daytradeQuantity != it.quantity
        }.fold(0) { total, sameTypeTransaction ->
            total + sameTypeTransaction.quantity
        }

        val otherTypeTransactionList = sameDayTransactions.filter {
            it.type != transaction.type && it.daytradeQuantity != it.quantity
        }

        var quantityStillAvailableForDaytrade = 0

        for (otherTypeTransaction in otherTypeTransactionList) {
            sameTypeTransactionAssetQuantity += otherTypeTransaction.quantity - otherTypeTransaction.daytradeQuantity
            if (sameTypeTransactionAssetQuantity > 0) {
                quantityStillAvailableForDaytrade = updatePastTransactionsForDaytrade(
                    otherTypeTransactions = otherTypeTransactionList.subList(
                        otherTypeTransactionList.indexOf(otherTypeTransaction),
                        otherTypeTransactionList.size
                    ),
                    lastQuantityStillAvailableForDaytrade = sameTypeTransactionAssetQuantity,
                    transactionQuantity = transaction.quantity
                )
                break
            }
        }

        return if (sameTypeTransactionAssetQuantity > 0)
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity - quantityStillAvailableForDaytrade
            )
        else transaction
    }

    private fun updatePastTransactionsForDaytrade(
        otherTypeTransactions: List<Transaction>,
        lastQuantityStillAvailableForDaytrade: Int,
        transactionQuantity: Int
    ): Int {
        var quantityLeftAvailableForDaytrading = transactionQuantity

        otherTypeTransactions.forEachIndexed { index, transaction ->
            if (quantityLeftAvailableForDaytrading > 0) {
                quantityLeftAvailableForDaytrading = if (index > 0) {
                    updatePastTransaction(
                        transaction = transaction,
                        quantityLeft = quantityLeftAvailableForDaytrading,
                        quantityAvailableToUse = transaction.quantity
                    )
                } else {
                    if (lastQuantityStillAvailableForDaytrade > 0) {
                        updatePastTransaction(
                            transaction = transaction,
                            quantityLeft = quantityLeftAvailableForDaytrading,
                            quantityAvailableToUse = lastQuantityStillAvailableForDaytrade
                        )
                    } else {
                        updatePastTransaction(
                            transaction = transaction,
                            quantityLeft = quantityLeftAvailableForDaytrading,
                            quantityAvailableToUse = transaction.quantity
                        )
                    }
                }
            }
        }

        return quantityLeftAvailableForDaytrading
    }

    private fun updatePastTransaction(
        transaction: Transaction,
        quantityLeft: Int,
        quantityAvailableToUse: Int
    ) = if (quantityLeft > quantityAvailableToUse) {
        this.transactionService.save(
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity
            )
        )
        quantityLeft - quantityAvailableToUse
    } else {
        this.transactionService.save(
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity - (quantityAvailableToUse - quantityLeft)
            )
        )
        0
    }

}