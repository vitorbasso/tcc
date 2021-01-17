package com.vitorbasso.gerenciadorinvestimentos.util

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.abs

@Component
object AccountantUtil {

    data class WalletReport(
        val newNormalValue: BigDecimal = BigDecimal.ZERO,
        val newDaytradeValue: BigDecimal = BigDecimal.ZERO,
        val newWithdrawn: BigDecimal = BigDecimal.ZERO,
        val newDaytradeWithdrawn: BigDecimal = BigDecimal.ZERO
    )

    data class AccountantReport(
        val walletsReport: Map<LocalDate, WalletReport> = mapOf(),
        val assetReport: BigDecimal = BigDecimal.ZERO,
        val transactionReport: List<Transaction> = listOf()
    )

    fun accountForNewTransaction(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>
    ): List<Transaction> {
        val accountedFor = mutableListOf<Transaction>()

        val (beforeTransactions, afterTransactions) = staleTransactions.partition {
            it.transactionDate.isBefore(newTransaction.transactionDate)
        }
        val (valueAtStart, quantityAtStart) = beforeTransactions.fold(Pair(BigDecimal.ZERO, 0)) { total, trans ->
            Pair(total.first.add(trans.value), total.second + trans.quantity)
        }

        calculateStuff(valueAtStart, quantityAtStart, afterTransactions.toMutableList().also { it.add(newTransaction) })


//        val (transactionsOnSameMonth, rest) = staleTransactions.partition {
//            it.transactionDate.atStartOfMonth() == staleTransactions.first().transactionDate.atStartOfMonth()
//        }

        val samedayTransactions = staleTransactions.filter {
            it.transactionDate.toLocalDate() == newTransaction.transactionDate.toLocalDate()
        }.toMutableList()
        samedayTransactions.add(newTransaction)
        return reprocessTransactionsForDaytrade(samedayTransactions)
    }

    private fun calculateStuff(initialValue: BigDecimal, initialQuantity: Int, transactions: List<Transaction>) {
        var totalValue = initialValue
        var totalQuantity = initialQuantity
        var withdrawn = BigDecimal.ZERO
        var withdrawnDaytrade = BigDecimal.ZERO
        var balance = BigDecimal.ZERO
        var balanceDaytrade = BigDecimal.ZERO
        var checkingQuantity = initialQuantity
        var state = false
        println("=============== NEW ONE ===============")
        transactions.forEach {
            when (it.type) {
                TransactionType.BUY -> {
                    if (checkingQuantity < 0) {
                        balance += getAverageCost(totalValue.abs(), abs(totalQuantity))
                            .subtract(getAverageCost(it.value, it.quantity))
                            .multiply(BigDecimal(
                                if (abs(checkingQuantity) < it.quantity) {
                                    state = true
                                    abs(checkingQuantity)
                                } else it.quantity
                            ))
                    } else {
                        totalValue += it.value
                        totalQuantity += it.quantity
                    }
                    checkingQuantity += it.quantity
                }
                TransactionType.SELL -> {
                    if (checkingQuantity > 0) {
                        balance += getAverageCost(it.value, it.quantity)
                            .subtract(getAverageCost(totalValue, totalQuantity))
                            .multiply(BigDecimal(
                                if (checkingQuantity < it.quantity) {
                                    state = true
                                    checkingQuantity
                                } else it.quantity
                            ))
                    } else {
                        totalQuantity -= it.quantity
                        totalValue -= it.value
                    }
                    checkingQuantity -= it.quantity
                }
            }
            println("totalValue = $totalValue - totalQuantity = $totalQuantity - checkingQuantity = $checkingQuantity - balance = $balance - state = $state")
            if(state || checkingQuantity == 0) {
                state = false
                totalQuantity = checkingQuantity
                totalValue = getAverageCost(it.value, it.quantity).multiply(BigDecimal(checkingQuantity))
                println("CHANGED: state = $state - totalQuantity = $totalQuantity - totalValue = $totalValue -> isSellout = true")
            }
            println("--------------")
        }
    }

    fun getTransactionNormalAndDaytradeValue(averageTickerValue: BigDecimal, transaction: Transaction) = Pair(
        averageTickerValue.multiply(
            BigDecimal(transaction.quantity).subtract(BigDecimal(transaction.daytradeQuantity)).abs()
        ),
        averageTickerValue.multiply(BigDecimal(transaction.daytradeQuantity))
    )

    private fun getAverageCost(value: BigDecimal = BigDecimal.ZERO, quantity: Int = 1) = value.divide(BigDecimal(quantity).takeIf { it.compareTo(BigDecimal.ZERO) > 0 }
        ?: BigDecimal.ONE, 20, RoundingMode.HALF_EVEN)

    fun reprocessTransactionsForDaytrade(sameDayTransactions: List<Transaction>): List<Transaction> {

        val changedTransactions = mutableListOf<Transaction>()

        val (buyTransactions, sellTransactions) = getBuyAndSellLists(sameDayTransactions)

        if (buyTransactions.isEmpty() || sellTransactions.isEmpty()) return buyTransactions + sellTransactions

        for (buyTransaction in buyTransactions) {
            if (!attemptToFillAvailableDaytradeQuantity(buyTransaction, sellTransactions, changedTransactions))
                break
        }

        return changedTransactions + sellTransactions + buyTransactions.filterNot {
            changedTransactions.map { changed -> changed.id }.contains(it.id)
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

private fun LocalDateTime.atStartOfMonth() = this.toLocalDate().withDayOfMonth(1)