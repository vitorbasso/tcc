package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService2
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
    private val accountingService: AccountingService2
) : ITransactionService {

    @Transactional
    override fun performTransaction(transactionsRequest: List<TransactionRequest>) =
        processTransaction(transactionsRequest.map { it.getTransaction() })

    @Transactional
    override fun deleteTransaction(transactionId: Long) {
        val transactionToDelete = this.transactionService.getTransaction(
            transactionId,
            SecurityContextUtil.getClientDetails().id
        )
        val transactions = this.transactionService.findAllByAsset(transactionToDelete.asset)
//        val accountantReport =
//            this.accountingService.accountFor(transactionToDelete, transactions, AccountingOperation.REMOVE_TRANSACTION)
//        this.transactionService.saveAll(accountantReport.transactionsReport)
        this.transactionService.deleteTransaction(transactionToDelete)
    }

    private fun processTransaction(transactions: List<Transaction>): List<Transaction> {
        if(transactions.isEmpty()) throw CustomBadRequestException(ManagerErrorCode.MANAGER_12)
        val staleTransactions = this.transactionService.findAllByAsset(transactions.first().asset)
        val newTransactions = this.transactionService.saveAll(transactions)
        val accountantReport = accountingService.accountForAddedTransactions(
            newTransactions,
            staleTransactions
        )

        return this.transactionService.saveAll(accountantReport)
    }

    private fun checkDate(dateToCheck: LocalDateTime, asset: Asset) = dateToCheck.takeIf {
        !it.toLocalDate().isAfter(LocalDate.now()) &&
            (it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY)
    } ?: throw CustomWrongDateException()

    private fun TransactionRequest.getTransaction() = assetService.getAsset(
        wallet = walletService.getWallet() as Wallet,
        stock = stockService.getStock(this.ticker)
    ).let {
        Transaction(
            type = this.type,
            quantity = this.quantity,
            value = this.value,
            asset = it,
            transactionDate = checkDate(this.date, it)
        )
    }

}