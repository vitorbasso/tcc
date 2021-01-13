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

    fun saveAndFlush(transaction: Transaction) = this.transactionRepository.saveAndFlush(transaction)

    fun saveAll(transactions: List<Transaction>) = this.transactionRepository.saveAll(transactions)

    fun findTransactionsOnSameDate(transaction: Transaction)
        = this.transactionRepository.findByAssetAndTransactionDateBetweenOrderByTransactionDate(
        transaction.asset,
        transaction.transactionDate.atStartOfDay(),
        transaction.transactionDate.plusDays(1).atStartOfDay()
    )

    fun findFromLastIsSellout(transaction: Transaction)
    = if(this.transactionRepository.existsByAssetAndTransactionDateGreaterThanEqual(
            transaction.asset,
            transaction.transactionDate
        ))
        this.transactionRepository.findFromLastIsSellout(
        transaction.asset.id,
        transaction.transactionDate
    ).takeIf { it.isNotEmpty() } ?: this.transactionRepository.findAllByAsset(transaction.asset)
    else
        findTransactionsOnSameDate(transaction)

    fun deleteTransaction(transaction: Transaction) = this.transactionRepository.delete(transaction)

}

private fun LocalDateTime.atStartOfDay()
= this.withHour(0).withMinute(0).withSecond(0).withNano(0)