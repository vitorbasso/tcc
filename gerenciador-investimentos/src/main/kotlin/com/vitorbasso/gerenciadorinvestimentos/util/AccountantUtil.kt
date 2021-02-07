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

        val (beforeNewTransaction, afterNewTransacion) = staleTransactions.partition {
            it.transactionDate.isBefore(newTransaction.transactionDate)
        }
        accountNormalBalance(staleTransactions)


        val daytradeProcessed = processDaytrade(
            beforeNewTransaction.filter {
                it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
            }
                + newTransaction + afterNewTransacion.filter {
                it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
            }
        ).sortedBy { it.transactionDate }
        val temp = accountNormalBalance((daytradeProcessed + staleTransactions.filter {
            !it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
        }).sortedBy { it.transactionDate }, accountedFor).toMutableMap()

        temp[newTransaction.transactionDate.atStartOfMonth()] =
            temp[newTransaction.transactionDate.atStartOfMonth()]?.copy(
                newDaytradeValue = accountDaytradeBalance(daytradeProcessed).subtract(
                    accountDaytradeBalance(
                        staleTransactions.filter {
                            it.transactionDate.toLocalDate().isEqual(newTransaction.transactionDate.toLocalDate())
                        })
                )
            ) ?: WalletReport()


        return AccountantReport(
            walletsReport = temp,
            transactionReport = accountedFor
        ).also {
            println("walletsReport = ${it.walletsReport}")
        }
    }

    private fun accountDaytradeBalance(
        transactions: List<Transaction>
    ): BigDecimal {
        val (buyTransactions, sellTransactions) = transactions.partition {
            it.type == TransactionType.BUY
        }
        val buyValue = buyTransactions.fold(BigDecimal.ZERO) { total, transaction ->
            total + getAverageCost(
                transaction.value,
                transaction.quantity
            ).multiply(BigDecimal(transaction.daytradeQuantity))
        }
        val sellValue = sellTransactions.fold(BigDecimal.ZERO) { total, transaction ->
            total + getAverageCost(
                transaction.value,
                transaction.quantity
            ).multiply(BigDecimal(transaction.daytradeQuantity))
        }
        return sellValue.subtract(buyValue)
    }

    private fun accountNormalBalance(
        transactions: List<Transaction>,
        updatedTransactions: MutableList<Transaction> = mutableListOf()
    ): Map<LocalDate, WalletReport> {
        val mapOfTransactions = mutableMapOf<LocalDate, List<Transaction>>()
        transactions.map { it.transactionDate.atStartOfMonth() to it }.forEach {
            mapOfTransactions[it.first] = mapOfTransactions[it.first]?.plus(listOf(it.second)) ?: listOf(it.second)
        }
        var quantityForAverage = transactions.firstOrNull()?.checkingQuantity ?: 0
        var valueForAverage = transactions.firstOrNull()?.checkingValue ?: BigDecimal.ZERO
        var quantityCount = transactions.firstOrNull()?.checkingQuantity ?: 0
        var balanceContribution = BigDecimal.ZERO

        return mapOfTransactions.map { map ->
            map.key to map.value.let { transactionList ->
                transactionList.forEach {
                    val transaction = it.copy(checkingQuantity = quantityForAverage, checkingValue = valueForAverage)
                    updatedTransactions.add(transaction)
                    val normalQuantity = transaction.quantity - transaction.daytradeQuantity
                    when (it.type) {
                        TransactionType.BUY -> {
                            when {
                                quantityCount >= 0 -> {
                                    valueForAverage = valueForAverage.add(
                                        getAverageCost(it.value, it.quantity).multiply(
                                            BigDecimal(normalQuantity)
                                        )
                                    )
                                    quantityForAverage += normalQuantity
                                }
                                abs(quantityCount) < normalQuantity -> {
                                    balanceContribution = balanceContribution.add(
                                        getAverageCost(it.value, it.quantity).subtract(
                                            getAverageCost(
                                                valueForAverage,
                                                quantityForAverage
                                            ).abs()
                                        ).multiply(BigDecimal(quantityCount))
                                    )
                                    quantityForAverage = normalQuantity - abs(quantityCount)
                                    valueForAverage =
                                        getAverageCost(it.value, it.quantity).multiply(BigDecimal(quantityForAverage))
                                }
                                else -> {
                                    balanceContribution = balanceContribution.add(
                                        getAverageCost(valueForAverage, quantityForAverage).abs()
                                            .subtract(getAverageCost(it.value, it.quantity))
                                            .multiply(BigDecimal(normalQuantity))
                                    )
                                }
                            }
                            quantityCount += normalQuantity
                        }
                        TransactionType.SELL -> {
                            when {
                                quantityCount <= 0 -> {
                                    valueForAverage = valueForAverage.subtract(
                                        getAverageCost(it.value, it.quantity).multiply(
                                            BigDecimal(normalQuantity)
                                        )
                                    )
                                    quantityForAverage -= normalQuantity
                                }
                                quantityCount < normalQuantity -> {
                                    balanceContribution = balanceContribution.add(
                                        getAverageCost(it.value, it.quantity).subtract(
                                            getAverageCost(
                                                valueForAverage,
                                                quantityForAverage
                                            )
                                        ).multiply(BigDecimal(quantityCount))
                                    )
                                    quantityForAverage = quantityCount - normalQuantity
                                    valueForAverage =
                                        getAverageCost(it.value, it.quantity).multiply(BigDecimal(quantityForAverage))
                                }
                                else -> {
                                    balanceContribution = balanceContribution.add(
                                        getAverageCost(it.value, it.quantity).subtract(
                                            getAverageCost(
                                                valueForAverage,
                                                quantityForAverage
                                            )
                                        ).multiply(BigDecimal(normalQuantity))
                                    )
                                }
                            }
                            quantityCount -= normalQuantity
                        }
                    }
                    if (quantityCount == 0) {
                        valueForAverage = BigDecimal.ZERO
                        quantityForAverage = 0
                    }
                }
                WalletReport(
                    newNormalValue = balanceContribution
                )
            }
        }.toMap()
    }

    fun getTransactionNormalAndDaytradeValue(averageTickerValue: BigDecimal, transaction: Transaction) = Pair(
        averageTickerValue.multiply(
            BigDecimal(transaction.quantity - transaction.daytradeQuantity).abs()
        ),
        averageTickerValue.multiply(BigDecimal(transaction.daytradeQuantity))
    )

    private fun getAverageCost(value: BigDecimal = BigDecimal.ZERO, quantity: Int = 1) =
        value.divide(BigDecimal(quantity).takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
            ?: BigDecimal.ONE, 20, RoundingMode.HALF_EVEN)

    fun processDaytrade(sameDayTransactions: List<Transaction>): List<Transaction> {

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
                modifiedTransactions.add(
                    typeTwoTransaction.copy(
                        daytrade = true,
                        daytradeQuantity = typeTwoTransaction.quantity
                    )
                )
                quantityAvailable -= typeTwoAvailableQuantity
            } else {
                typeTwoTransactions.add(
                    0, typeTwoTransaction.copy(
                        daytrade = true,
                        daytradeQuantity = typeTwoTransaction.daytradeQuantity + quantityAvailable
                    )
                )
                quantityAvailable = 0
            }
        }
        modifiedTransactions.add(
            typeOneTransaction.copy(
                daytrade = true,
                daytradeQuantity = typeOneTransaction.quantity - quantityAvailable
            )
        )
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