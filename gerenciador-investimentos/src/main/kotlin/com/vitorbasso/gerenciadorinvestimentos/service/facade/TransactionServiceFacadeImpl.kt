package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import com.vitorbasso.gerenciadorinvestimentos.util.AccountantUtil
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Service
internal class TransactionServiceFacadeImpl(
    private val transactionService: TransactionService,
    private val stockService: StockService,
    private val assetService: AssetServiceFacadeImpl,
    private val walletService: WalletServiceFacadeImpl,
    private val monthlyWalletService: MonthlyWalletServiceFacadeImpl
) : ITransactionService {

    @Transactional
    override fun performTransaction(transactionRequest: TransactionRequest) = this.assetService.addTransactionToAsset(
        wallet = this.walletService.getWallet(transactionRequest.walletId),
        stock = this.stockService.getStock(transactionRequest.ticker),
        amount = transactionRequest.quantity,
        cost = transactionRequest.value,
        type = transactionRequest.type
    ).let {
        transactionRequest.getTransaction(it)
    }.let {
        processTransaction(it)
    }

    @Transactional
    override fun deleteTransaction(transactionId: Long) {
        val transactionToDelete = this.transactionService.getTransaction(
            transactionId,
            SecurityContextUtil.getClientDetails().id
        )
        val transactions = this.transactionService.findTransactionsOnSameDate(transactionToDelete).toMutableList()
        transactions.remove(transactionToDelete)
        this.transactionService.saveAll(AccountantUtil.processDaytrade(transactions))
        this.transactionService.deleteTransaction(transactionToDelete)
    }

    private fun processTransaction(transaction: Transaction): Transaction {
        val accountantReport = AccountantUtil.accountForNewTransaction(
            transaction,
            this.transactionService.findFromLastIsSellout(transaction)
        )
        this.transactionService.saveAll(accountantReport.transactionsReport)

        this.walletService.processWalletReport(
            transaction.asset.wallet,
            accountantReport,
            this.monthlyWalletService
        )

        this.assetService.processAssetReport(
            transaction.asset,
            accountantReport
        )

        return accountantReport.transactionsReport.findLast { it.transactionDate.isEqual(transaction.transactionDate) }
            ?: transaction
    }

    private fun checkDate(dateToCheck: LocalDateTime) = dateToCheck.takeIf {
        !it.toLocalDate().isAfter(LocalDate.now()) &&
            (it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY)
    } ?: throw CustomWrongDateException()

    private fun TransactionRequest.getTransaction(asset: Asset) = Transaction(
        type = this.type,
        quantity = this.quantity,
        value = this.value,
        asset = asset,
        transactionDate = checkDate(this.date)
    )

}