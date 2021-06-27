package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.util.parallelMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class AccountingService {

    fun accountForAddedTransactions(
        newTransactions: List<Transaction>,
        existingTransactions: List<Transaction>
    ): Map<Long, List<Transaction>> {

        val newTransactionsByAsset = newTransactions.mapByAsset()
        val existingTransactionsByAsset = existingTransactions.mapByAsset()

        return runBlocking(Dispatchers.IO) {
            newTransactionsByAsset.entries.parallelMap { (asset, transactions) ->
                val newTransactionsDateSet = transactions.map { it.transactionDate.toLocalDate() }.toSet()

                val (transactionsToProcessForDaytrade, _) =
                    (existingTransactionsByAsset[asset] ?: listOf())
                        .partition { newTransactionsDateSet.contains(it.transactionDate.toLocalDate()) }

                val processedTransactionsForDaytrade =
                    transactionsToProcessForDaytrade
                        .plus(transactions)
                        .groupByDate()
                        .parallelMap { DaytradeService.processDaytrade(it) }.flatten()

                asset to processedTransactionsForDaytrade
            }.toMap()
        }
    }

}

private fun List<Transaction>.mapByAsset() =
    mutableMapOf<Long, List<Transaction>>().let { map ->
        this.map { it.asset.id to it }.forEach {
            map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
        }
        map
    }

private fun List<Transaction>.groupByDate() =
    mutableMapOf<LocalDate, List<Transaction>>().let { map ->
        this.map { it.transactionDate.toLocalDate() to it }.forEach {
            map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
        }
        map
    }.values.toList()