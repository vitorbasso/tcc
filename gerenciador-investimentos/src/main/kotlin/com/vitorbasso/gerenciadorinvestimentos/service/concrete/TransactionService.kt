package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
internal class TransactionService(
        val transactionRepository: ITransactionRepository
) {

    fun getTransaction(transactionId: Long) = this.transactionRepository.findByIdOrNull(transactionId)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun save(transaction: Transaction) = this.transactionRepository.save(transaction)

    fun findTransactionsOnSameDate(transaction: Transaction)
        = this.transactionRepository.findByAssetAndTransactionDateOrderByTransactionDate(
        transaction.asset,
        transaction.transactionDate
    )

    fun findTransactionsOnSameMonth(transaction: Transaction)
        = this.transactionRepository.findByMonth(
        transaction.asset,
        transaction.transactionDate.month.ordinal + 1
    )

    fun deleteTransaction(transaction: Transaction) = this.transactionRepository.delete(transaction)

}