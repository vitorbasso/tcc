package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.service.IAccountingServiceSubscriber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class AccountingService2(
    private val subscribers: List<IAccountingServiceSubscriber>
) {

    fun accountForAddedTransactions(
        newTransactions: List<Transaction>,
        existingTransactions: List<Transaction>
    ) : List<Transaction> {

        val newTransactionsDates = newTransactions.map { it.transactionDate.toLocalDate() }.toSet()
        val (transactionsToReprocessForDaytrade, _) =
            existingTransactions.partition { newTransactionsDates.contains(it.transactionDate.toLocalDate()) }

        return runBlocking(Dispatchers.IO) {
            transactionsToReprocessForDaytrade.plus(newTransactions).mapByDate().values.parallelMap {
                DaytradeService.processDaytrade(it)
            }
        }.flatten().sortedBy { it.transactionDate }

    }

}

private fun List<Transaction>.mapByDate() =
    mutableMapOf<LocalDate, List<Transaction>>().let { map ->
        this.map { it.transactionDate.toLocalDate() to it }.forEach {
            map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
        }
        map
    }

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}