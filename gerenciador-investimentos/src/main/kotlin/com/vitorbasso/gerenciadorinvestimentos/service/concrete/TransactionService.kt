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

    fun getTransaction(transactionId: Long, clientId: Long)
    = this.transactionRepository.findByIdOrNull(transactionId)?.takeIf { it.asset.wallet.client.id == clientId }
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun save(transaction: Transaction) = this.transactionRepository.save(transaction)

    fun saveAndFlush(transaction: Transaction) = this.transactionRepository.saveAndFlush(transaction)

    fun saveAll(transactions: List<Transaction>) = this.transactionRepository.saveAll(transactions)

    fun findTransactionsOnSameDate(transaction: Transaction)
        = this.transactionRepository.findByAssetAndTransactionDateOrderByTransactionDate(
        transaction.asset,
        transaction.transactionDate
    )

    fun findFromLastIsSellout(transaction: Transaction)
    = this.transactionRepository.findFromLastIsSellout(
        transaction.asset.id,
        transaction.transactionDate
    )

    fun deleteTransaction(transaction: Transaction) = this.transactionRepository.delete(transaction)

}