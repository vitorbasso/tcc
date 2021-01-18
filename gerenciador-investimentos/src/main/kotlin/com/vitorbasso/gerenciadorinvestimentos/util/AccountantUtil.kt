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

    private fun WalletReport.subtract(other: WalletReport?) = WalletReport(
        newNormalValue = this.newNormalValue.subtract(other?.newNormalValue ?: BigDecimal.ZERO),
        newDaytradeValue = this.newDaytradeValue.subtract(other?.newDaytradeValue ?: BigDecimal.ZERO),
        newWithdrawn = this.newWithdrawn.subtract(other?.newWithdrawn ?: BigDecimal.ZERO),
        newDaytradeWithdrawn = this.newDaytradeWithdrawn.subtract(other?.newDaytradeWithdrawn ?: BigDecimal.ZERO),
    )

    data class AccountantReport(
        val walletsReport: Map<LocalDate, WalletReport> = mapOf(),
        val assetReport: BigDecimal = BigDecimal.ZERO,
        val transactionReport: List<Transaction> = listOf()
    )

    fun accountForNewTransaction(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>
    ): AccountantReport {
        val accountedFor = mutableListOf<Transaction>()

        val (before, after) = staleTransactions.partition {
            it.transactionDate.isBefore(newTransaction.transactionDate)
        }
        val (beforeNotSameDay, beforeSameDay) = before.partition {
            !it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
        }

        val (afterNotSameDay, afterSameDay) = after.partition {
            !it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
        }

        val temp = reprocessTransactionsForDaytrade(beforeSameDay + newTransaction + afterSameDay).toMutableList()

        val teste = (beforeNotSameDay + temp + afterNotSameDay).sortedBy { it.transactionDate }

        val temp1 = calculateStuff(transactions = staleTransactions)

        val temp2 = calculateStuff(transactions = teste, changedTransactions = accountedFor)

        return AccountantReport(
            transactionReport = accountedFor,
            walletsReport = temp2.mapValues {
                it.value.subtract(temp1[it.key])
            }
        )
    }

    private fun calculateStuff(
        initialValue: BigDecimal = BigDecimal.ZERO,
        initialQuantity: Int = 0,
        transactions: List<Transaction>,
        changedTransactions: MutableList<Transaction> = mutableListOf()
    ): Map<LocalDate, WalletReport> {
        val mapOfTransactions = mutableMapOf<LocalDate, List<Transaction>>()
        transactions.map { it.transactionDate.atStartOfMonth() to it }.forEach {
            mapOfTransactions[it.first] = mapOfTransactions[it.first]?.plus(listOf(it.second))?: listOf(it.second)
        }

        var totalValue = initialValue
        var totalQuantity = initialQuantity
        var withdrawn = BigDecimal.ZERO
        var withdrawnDaytrade = BigDecimal.ZERO
        var balance = BigDecimal.ZERO
        var balanceDaytrade = BigDecimal.ZERO
        var checkingQuantity = initialQuantity
        var state = false

        val report = mapOfTransactions.map { map ->
            map.key to map.value.let { transactionsList ->
                transactionsList.forEach {
                    val normalQuantity = it.quantity - it.daytradeQuantity
                    when (it.type) {
                        TransactionType.BUY -> {
                            if (checkingQuantity < 0) {
                                balanceDaytrade += getAverageCost(totalValue.abs(), abs(totalQuantity))
                                    .subtract(getAverageCost(it.value, it.quantity))
                                    .multiply(BigDecimal(it.daytradeQuantity))
                                if (checkingQuantity < 0) {
                                    balance += getAverageCost(totalValue.abs(), abs(totalQuantity))
                                        .subtract(getAverageCost(it.value, it.quantity))
                                        .multiply(BigDecimal(
                                            if (abs(checkingQuantity) <= normalQuantity) {
                                                state = true
                                                abs(checkingQuantity)
                                            } else normalQuantity
                                        ))
                                }
                            } else {
                                totalValue += it.value
                                totalQuantity += it.quantity
                            }
                            checkingQuantity += normalQuantity
                        }
                        TransactionType.SELL -> {
                            if (checkingQuantity > 0) {
                                balanceDaytrade += getAverageCost(it.value, it.quantity)
                                    .subtract(getAverageCost(totalValue, totalQuantity))
                                    .multiply(BigDecimal(it.daytradeQuantity))
                                if (checkingQuantity > 0) {
                                    balance += getAverageCost(it.value, it.quantity)
                                        .subtract(getAverageCost(totalValue, totalQuantity))
                                        .multiply(BigDecimal(
                                            if (checkingQuantity <= normalQuantity) {
                                                state = true
                                                checkingQuantity
                                            } else normalQuantity
                                        ))
                                }
                            } else {
                                totalQuantity -= it.quantity
                                totalValue -= it.value
                            }
                            checkingQuantity -= normalQuantity
                            withdrawn += getAverageCost(it.value, it.quantity).multiply(BigDecimal(normalQuantity))
                            withdrawnDaytrade += getAverageCost(it.value, it.quantity).multiply(BigDecimal(it.daytradeQuantity))
                        }
                    }
                    if (state) {
                        state = false
                        totalQuantity = checkingQuantity
                        totalValue = getAverageCost(it.value, it.quantity).multiply(BigDecimal(checkingQuantity))
                        changedTransactions.add(it.copy(isSellout = true))
                    } else {
                        changedTransactions.add(it.copy(isSellout = false))
                    }
                }
                val report = WalletReport(
                    newNormalValue = balance,
                    newDaytradeValue = balanceDaytrade,
                    newWithdrawn = withdrawn,
                    newDaytradeWithdrawn = withdrawnDaytrade
                )
                withdrawn = BigDecimal.ZERO
                withdrawnDaytrade = BigDecimal.ZERO
                balance = BigDecimal.ZERO
                balanceDaytrade = BigDecimal.ZERO
                report
            }
        }.toMap()

        return report
    }

    fun getTransactionNormalAndDaytradeValue(averageTickerValue: BigDecimal, transaction: Transaction) = Pair(
        averageTickerValue.multiply(
            BigDecimal(transaction.quantity - transaction.daytradeQuantity).abs()
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

private fun LocalDateTime.atStartOfDay() = this.withHour(0).withMinute(0).withSecond(0).withNano(0)

private fun LocalDateTime.atStartOfMonth() = this.toLocalDate().withDayOfMonth(1)