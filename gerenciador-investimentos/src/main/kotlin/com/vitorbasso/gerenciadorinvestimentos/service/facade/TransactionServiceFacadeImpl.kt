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
        this.transactionService.save(it)
    }

    private fun isDaytrade(asset: Asset, transaction: Transaction)
        = this.transactionService.findTransactionsOnDate(asset, transaction.transactionDate).let {transactionsOnDate ->
        transactionsOnDate.any { it.type != transaction.type }
            && quantityChecksOut(transactionsOnDate, transaction)
    }

    private fun quantityChecksOut(transactionsOnDate: List<Transaction>, transactionToCheck: Transaction)
        = transactionsOnDate.fold(0) { total, transactionOnDate ->
        when (transactionOnDate.type) {
            TransactionType.BUY -> total + transactionOnDate.quantity
            TransactionType.SELL -> total - transactionOnDate.quantity
        }
    }.let {
        when (transactionToCheck.type) {
            TransactionType.SELL -> it + transactionToCheck.quantity > 0
            TransactionType.BUY -> it - transactionToCheck.quantity < 0
        }
    }

}