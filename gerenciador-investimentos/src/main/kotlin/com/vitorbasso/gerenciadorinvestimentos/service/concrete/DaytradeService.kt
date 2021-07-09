package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import org.springframework.stereotype.Service

@Service
internal object DaytradeService {

    suspend fun processDaytrade(sameDayTransactions: List<Transaction>): List<Transaction> {

        val processedTransactions = mutableListOf<Transaction>()

        val (buyTransactions, sellTransactions) = getBuyAndSellLists(sameDayTransactions)

        if (buyTransactions.isEmpty() || sellTransactions.isEmpty()) return buyTransactions + sellTransactions

        for (buyTransaction in buyTransactions) {
            if (!attemptToFillAvailableDaytradeQuantity(buyTransaction, sellTransactions, processedTransactions))
                break
        }

        return processedTransactions + sellTransactions + buyTransactions.filterNot {
            processedTransactions.map { changed -> changed.id }.contains(it.id)
        }
    }

    private fun attemptToFillAvailableDaytradeQuantity(
        typeOneTransaction: Transaction,
        typeTwoTransactions: MutableList<Transaction>,
        modifiedTransactions: MutableList<Transaction>
    ): Boolean {
        var quantityAvailable = typeOneTransaction.quantity - typeOneTransaction.daytradeQuantity
        while (quantityAvailable > 0 && typeTwoTransactions.isNotEmpty()) {
            val typeTwoTransaction = typeTwoTransactions.removeFirst()
            val typeTwoAvailableQuantity = typeTwoTransaction.quantity - typeTwoTransaction.daytradeQuantity
            if (typeTwoAvailableQuantity <= quantityAvailable) {
                modifiedTransactions.add(
                    typeTwoTransaction.copy(
                        daytradeQuantity = typeTwoTransaction.quantity
                    )
                )
                quantityAvailable -= typeTwoAvailableQuantity
            } else {
                typeTwoTransactions.add(
                    0, typeTwoTransaction.copy(
                        daytradeQuantity = typeTwoTransaction.daytradeQuantity + quantityAvailable
                    )
                )
                quantityAvailable = 0
            }
        }
        modifiedTransactions.add(
            typeOneTransaction.copy(
                daytradeQuantity = typeOneTransaction.quantity - quantityAvailable
            )
        )
        return typeTwoTransactions.isNotEmpty()
    }

    private fun getBuyAndSellLists(sameDayTransactions: List<Transaction>) = (sameDayTransactions.map {
        it.copy(
            daytradeQuantity = 0
        )
    }.partition {
        it.type == TransactionType.BUY
    }).let { Pair(it.first.toMutableList(), it.second.toMutableList()) }

}