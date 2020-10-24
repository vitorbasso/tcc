package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class TransactionService(
        val transactionRepository: ITransactionRepository
) {

    fun save(transaction: Transaction) = this.transactionRepository.save(transaction)

    fun findTransactionsOnDate(asset: Asset, date: LocalDate = LocalDate.now())
        = this.transactionRepository.findByAssetAndTransactionDateOrderByTransactionDate(asset, date)

}