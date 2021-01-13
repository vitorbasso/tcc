package com.vitorbasso.gerenciadorinvestimentos.util

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
object AccountantUtil {

    private const val NORMAL_VALUE = "normal"
    private const val DAYTRADE_VALUE = "daytrade"

    fun accountForNewTransaction(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>,
        sameDayTransactions: List<Transaction>
    ) : List<Transaction> {
        staleTransactions.fold(mapOf(NORMAL_VALUE to BigDecimal.ZERO, DAYTRADE_VALUE to BigDecimal.ZERO)){ total, transaction ->
            val (normalValue, daytradeValue) = getTransactionNormalAndDaytradeValue(
                getAverageTickerValue(transaction),
                transaction
            )
            mapOf(
                NORMAL_VALUE to total[NORMAL_VALUE]?.add(normalValue),
                DAYTRADE_VALUE to total[DAYTRADE_VALUE]?.add(daytradeValue)
            )
        }
        val samedayTransactions = staleTransactions.filter {
            it.transactionDate.toLocalDate() == newTransaction.transactionDate.toLocalDate()
        }
        return reprocessTransactionsForDaytrade(samedayTransactions)
    }

    private fun getAverageTickerValue(transaction: Transaction)
    = transaction.value.divide(BigDecimal(transaction.quantity), 20, RoundingMode.HALF_EVEN)

    fun getTransactionNormalAndDaytradeValue(averageTickerValue: BigDecimal, transaction: Transaction) = Pair(
        averageTickerValue.multiply(
            BigDecimal(transaction.quantity).subtract(BigDecimal(transaction.daytradeQuantity)).abs()
        ),
        averageTickerValue.multiply(BigDecimal(transaction.daytradeQuantity))
    )

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