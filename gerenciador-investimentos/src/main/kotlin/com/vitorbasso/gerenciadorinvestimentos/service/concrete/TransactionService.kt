package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
internal class TransactionService(
    val transactionRepository: ITransactionRepository
) {

    fun getTransaction(transactionId: Long, clientId: Long)
    = this.transactionRepository.findByIdOrNull(transactionId)?.takeIf { it.asset.wallet.client.id == clientId }
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun save(transaction: Transaction) = this.transactionRepository.save(transaction)

    fun saveAll(transactions: List<Transaction>) : List<Transaction> = this.transactionRepository.saveAll(transactions)

    fun findFromOneBeforeTransactionDate(transaction: Transaction)
    = this.transactionRepository.findAllFromTransactionBeforeTransactionDate(
        transaction.asset.id,
        transaction.transactionDate.atStartOfDay()
    ).takeIf { it.isNotEmpty() } ?: this.transactionRepository.findAllByAssetOrderByTransactionDate(transaction.asset)

    fun findTransactionsOnSameDate(transaction: Transaction)
    = this.transactionRepository.findByAssetAndTransactionDateBetweenOrderByTransactionDate(
        transaction.asset,
        transaction.transactionDate.atStartOfDay(),
        transaction.transactionDate.plusDays(1).atStartOfDay()
    )

    fun deleteTransaction(transaction: Transaction) = this.transactionRepository.delete(transaction)

}

private fun LocalDateTime.atStartOfDay() = this.withHour(0).withMinute(0).withSecond(0).withNano(0)