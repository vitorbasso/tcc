package com.vitorbasso.gerenciadorinvestimentos.util

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import org.springframework.stereotype.Component

@Component
object AccountantUtil {

    fun reprocessTransactionsForDaytrade(sameDayTransactions: List<Transaction>): List<Transaction>{

        val changedTransactions = mutableListOf<Transaction>()

        val (buyTransactions, sellTransactions) = getBuyAndSellLists(sameDayTransactions)

        if(buyTransactions.isEmpty() || sellTransactions.isEmpty()) return buyTransactions+sellTransactions

        for(buyTransaction in buyTransactions) {
            if(!attemptToFillAvailableDaytradeQuantity(buyTransaction, sellTransactions, changedTransactions))
                break
        }

        return changedTransactions + sellTransactions + buyTransactions.filterNot {
            changedTransactions.map { changed-> changed.id }.contains(it.id)
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
            if(typeTwoAvailableQuantity <= quantityAvailable) {
                modifiedTransactions.add(typeTwoTransaction.copy(
                    daytrade = true,
                    daytradeQuantity = typeTwoTransaction.quantity
                ))
                quantityAvailable -= typeTwoAvailableQuantity
            } else {
                typeTwoTransactions.add(0, typeTwoTransaction.copy(
                    daytrade = true,
                    daytradeQuantity = typeTwoTransaction.daytradeQuantity + quantityAvailable
                ))
                quantityAvailable = 0
            }
        }
        modifiedTransactions.add(typeOneTransaction.copy(
            daytrade = true,
            daytradeQuantity = typeOneTransaction.quantity - quantityAvailable
        ))
        return typeTwoTransactions.isNotEmpty()
    }

    private fun getBuyAndSellLists(sameDayTransactions: List<Transaction>) = (sameDayTransactions.map {
        it.copy(
            daytrade = false,
            daytradeQuantity = 0
        )
    }.partition {
        it.type == TransactionType.BUY
    }).let { Pair(it.first.toMutableList(), it.second.toMutableList()) }

}