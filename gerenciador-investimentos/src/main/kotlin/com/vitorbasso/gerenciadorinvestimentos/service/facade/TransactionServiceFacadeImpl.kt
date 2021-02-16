package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.AccountingOperation
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Service
internal class TransactionServiceFacadeImpl(
    private val transactionService: TransactionService,
    private val stockService: IStockService,
    private val assetService: IAssetService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    private val accountingService: AccountingService
) : ITransactionService {

    @Transactional
    override fun performTransaction(transactionRequest: TransactionRequest) =
        processTransaction(transactionRequest.getTransaction())

    @Transactional
    override fun deleteTransaction(transactionId: Long) {
        val transactionToDelete = this.transactionService.getTransaction(
            transactionId,
            SecurityContextUtil.getClientDetails().id
        )
        val transactions = this.transactionService.findFromOneBeforeTransactionDate(transactionToDelete)
        val accountantReport =
            this.accountingService.accountFor(transactionToDelete, transactions, AccountingOperation.REMOVE_TRANSACTION)
        this.transactionService.saveAll(accountantReport.transactionsReport)
        this.transactionService.deleteTransaction(transactionToDelete)
    }

    private fun processTransaction(transaction: Transaction): Transaction {
        val staleTransactions = this.transactionService.findFromOneBeforeTransactionDate(transaction)
        val newTransaction = this.transactionService.save(transaction)
        val accountantReport = accountingService.accountFor(
            newTransaction,
            staleTransactions
        )

        val saved = this.transactionService.saveAll(accountantReport.transactionsReport)

        return saved.findLast { it.id == newTransaction.id } ?: newTransaction
    }

    private fun checkDate(dateToCheck: LocalDateTime) = dateToCheck.takeIf {
        !it.toLocalDate().isAfter(LocalDate.now()) &&
            (it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY)
    } ?: throw CustomWrongDateException()

    private fun TransactionRequest.getTransaction() = Transaction(
        type = this.type,
        quantity = this.quantity,
        value = this.value,
        asset = assetService.getAsset(
            wallet = walletService.getWallet(this.walletId) as Wallet,
            stock = stockService.getStock(this.ticker)
        ),
        transactionDate = checkDate(this.date)
    )

}