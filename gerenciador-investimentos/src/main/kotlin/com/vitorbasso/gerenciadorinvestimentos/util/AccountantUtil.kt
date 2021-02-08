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
        val balanceContribution: BigDecimal = BigDecimal.ZERO,
        val daytradeBalanceContribution: BigDecimal = BigDecimal.ZERO,
        val withdrawnContribution: BigDecimal = BigDecimal.ZERO,
        val daytradeWithdrawnContribution: BigDecimal = BigDecimal.ZERO
    )

    private fun WalletReport.subtract(other: WalletReport?) = WalletReport(
        balanceContribution = this.balanceContribution.subtract(other?.balanceContribution ?: BigDecimal.ZERO),
        daytradeBalanceContribution = this.daytradeBalanceContribution.subtract(
            other?.daytradeBalanceContribution ?: BigDecimal.ZERO
        ),
        withdrawnContribution = this.withdrawnContribution.subtract(other?.withdrawnContribution ?: BigDecimal.ZERO),
        daytradeWithdrawnContribution = this.daytradeWithdrawnContribution.subtract(
            other?.daytradeWithdrawnContribution ?: BigDecimal.ZERO
        ),
    )

    data class AccountantReport(
        val walletsReport: Map<LocalDate, WalletReport> = mapOf(),
        val assetReport: BigDecimal = BigDecimal.ZERO,
        val transactionsReport: List<Transaction> = listOf(),
        val lifetimeBalanceChange: BigDecimal = BigDecimal.ZERO
    )

    private data class AccountantNotes(
        var quantityForAverage: Int = 0,
        var valueForAverage: BigDecimal = BigDecimal.ZERO,
        var quantityCount: Int = 0,
        var balanceContribution: BigDecimal = BigDecimal.ZERO,
        var withdrawnContribution: BigDecimal = BigDecimal.ZERO
    )

    fun accountForNewTransaction(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>
    ): AccountantReport {
        val accountedFor = mutableListOf<Transaction>()

        val (sameDayTransactions, otherDayTransactions)
            = staleTransactions.partitionByDate(newTransaction.transactionDate.toLocalDate())

        val beforeNewTransaction = calculateContribution(staleTransactions, sameDayTransactions).first

        val daytradeProcessed = processDaytrade(
            (sameDayTransactions + newTransaction).sortedBy { it.transactionDate }
        )

        val (walletsReport, assetReport) = calculateContribution(
            transactions = (daytradeProcessed + otherDayTransactions).sortedBy { it.transactionDate },
            daytradeTransactions = daytradeProcessed,
            accountedFor = accountedFor
        )

        val lifetimeBalanceChange =
            (daytradeProcessed + otherDayTransactions).fold(BigDecimal.ZERO) { total, transaction ->
                when (transaction.type) {
                    TransactionType.BUY -> total.subtract(transaction.value)
                    TransactionType.SELL -> total.add(transaction.value)
                }
            }.subtract(staleTransactions.fold(BigDecimal.ZERO) { total, transaction ->
                when (transaction.type) {
                    TransactionType.BUY -> total.subtract(transaction.value)
                    TransactionType.SELL -> total.add(transaction.value)
                }
            }
            )

        return AccountantReport(
            walletsReport = walletsReport.map {
                it.key to it.value.subtract(beforeNewTransaction[it.key])
            }.toMap(),
            transactionsReport = accountedFor,
            assetReport = assetReport,
            lifetimeBalanceChange = lifetimeBalanceChange
        )
    }

    private fun calculateContribution(
        transactions: List<Transaction>,
        daytradeTransactions: List<Transaction>,
        accountedFor: MutableList<Transaction> = mutableListOf()
    ): Pair<Map<LocalDate, WalletReport>, BigDecimal> {
        val transactionsByMonth = mapTransactionsToMonth(transactions)

        val accountantNotes = AccountantNotes(
            quantityForAverage = transactions.firstOrNull()?.checkingQuantity ?: 0,
            valueForAverage = transactions.firstOrNull()?.checkingValue ?: BigDecimal.ZERO,
            quantityCount = transactions.firstOrNull()?.checkingQuantity ?: 0
        )

        return transactionsByMonth.map { map ->
            map.key to map.value.let { transactionList ->
                accountantNotes.balanceContribution = BigDecimal.ZERO
                accountantNotes.withdrawnContribution = BigDecimal.ZERO
                transactionList.forEach {
                    val transaction = it.copy(
                        checkingQuantity = accountantNotes.quantityForAverage,
                        checkingValue = accountantNotes.valueForAverage
                    )
                    accountedFor.add(transaction)
                    calculateTransactionContribution(
                        transaction = transaction,
                        accountantNotes = accountantNotes
                    )
                }

                val (daytradeBalance, daytradeWithdrawn)
                    = if (isDaytradeMonth(map, daytradeTransactions))
                    calculateContributionDaytrade(daytradeTransactions)
                else
                    Pair(BigDecimal.ZERO, BigDecimal.ZERO)

                WalletReport(
                    balanceContribution = accountantNotes.balanceContribution,
                    withdrawnContribution = accountantNotes.withdrawnContribution,
                    daytradeBalanceContribution = daytradeBalance,
                    daytradeWithdrawnContribution = daytradeWithdrawn
                )
            }
        }.toMap() to (
            getAverageCost(accountantNotes.valueForAverage, accountantNotes.quantityForAverage).takeIf {
                accountantNotes.quantityCount > 0
            } ?: BigDecimal.ZERO
            )
    }

    private fun calculateTransactionContribution(
        transaction: Transaction,
        accountantNotes: AccountantNotes
    ) {
        when (transaction.type) {
            TransactionType.BUY -> processBuyTransactionContribution(transaction, accountantNotes)
            TransactionType.SELL -> processSellTransactionContribution(transaction, accountantNotes)
        }
        if (accountantNotes.quantityCount == 0) {
            accountantNotes.valueForAverage = BigDecimal.ZERO
            accountantNotes.quantityForAverage = 0
        }
    }

    private fun processBuyTransactionContribution(
        transaction: Transaction,
        accountantNotes: AccountantNotes
    ) {
        val normalQuantity = transaction.quantity - transaction.daytradeQuantity
        when {
            accountantNotes.quantityCount >= 0 -> {
                accountantNotes.valueForAverage = accountantNotes.valueForAverage.add(
                    getAverageCost(transaction.value, transaction.quantity).multiply(
                        BigDecimal(normalQuantity)
                    )
                )
                accountantNotes.quantityForAverage += normalQuantity
            }
            abs(accountantNotes.quantityCount) < normalQuantity -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    getAverageCost(transaction.value, transaction.quantity).subtract(
                        getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        ).abs()
                    ).multiply(BigDecimal(accountantNotes.quantityCount))
                )
                accountantNotes.quantityForAverage = normalQuantity - abs(accountantNotes.quantityCount)
                accountantNotes.valueForAverage =
                    getAverageCost(
                        transaction.value,
                        transaction.quantity
                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
            }
            else -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    getAverageCost(accountantNotes.valueForAverage, accountantNotes.quantityForAverage).abs()
                        .subtract(getAverageCost(transaction.value, transaction.quantity))
                        .multiply(BigDecimal(normalQuantity))
                )
            }
        }
        accountantNotes.quantityCount += normalQuantity
    }

    private fun processSellTransactionContribution(
        transaction: Transaction,
        accountantNotes: AccountantNotes
    ) {
        val normalQuantity = transaction.quantity - transaction.daytradeQuantity
        accountantNotes.withdrawnContribution = accountantNotes.withdrawnContribution.add(
            getAverageCost(transaction.value, transaction.quantity).multiply(
                BigDecimal(normalQuantity)
            )
        )
        when {
            accountantNotes.quantityCount <= 0 -> {
                accountantNotes.valueForAverage = accountantNotes.valueForAverage.subtract(
                    getAverageCost(transaction.value, transaction.quantity).multiply(
                        BigDecimal(normalQuantity)
                    )
                )
                accountantNotes.quantityForAverage -= normalQuantity
            }
            accountantNotes.quantityCount < normalQuantity -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    getAverageCost(transaction.value, transaction.quantity).subtract(
                        getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        )
                    ).multiply(BigDecimal(accountantNotes.quantityCount))
                )
                accountantNotes.quantityForAverage = accountantNotes.quantityCount - normalQuantity
                accountantNotes.valueForAverage =
                    getAverageCost(
                        transaction.value,
                        transaction.quantity
                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
            }
            else -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    getAverageCost(transaction.value, transaction.quantity).subtract(
                        getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        )
                    ).multiply(BigDecimal(normalQuantity))
                )
            }
        }
        accountantNotes.quantityCount -= normalQuantity
    }

    private fun calculateContributionDaytrade(
        transactions: List<Transaction>
    ): Pair<BigDecimal, BigDecimal> {
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
        return Pair(sellValue.subtract(buyValue), sellValue)
    }

    fun processDaytrade(sameDayTransactions: List<Transaction>): List<Transaction> {

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

    private fun isDaytradeMonth(
        map: Map.Entry<LocalDate, List<Transaction>>,
        daytradeTransactions: List<Transaction>
    ) = map.key.isEqual(
        daytradeTransactions.firstOrNull()?.transactionDate?.atStartOfMonth() ?: map.key.withDayOfMonth(2)
    )

    private fun mapTransactionsToMonth(transactions: List<Transaction>) =
        mutableMapOf<LocalDate, List<Transaction>>().let { map ->
            transactions.map { it.transactionDate.atStartOfMonth() to it }.forEach {
                map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
            }
            map
        }

    private fun getAverageCost(value: BigDecimal = BigDecimal.ZERO, quantity: Int = 1) =
        value.divide(BigDecimal(quantity).takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
            ?: BigDecimal.ONE, 20, RoundingMode.HALF_EVEN)

    private fun getBuyAndSellLists(sameDayTransactions: List<Transaction>) = (sameDayTransactions.map {
        it.copy(
            daytrade = false,
            daytradeQuantity = 0
        )
    }.partition {
        it.type == TransactionType.BUY
    }).let { Pair(it.first.toMutableList(), it.second.toMutableList()) }

}

private fun List<Transaction>.partitionByDate(date: LocalDate) =
    this.partition { it.transactionDate.toLocalDate().isEqual(date) }

private fun LocalDateTime.atStartOfMonth() = this.toLocalDate().withDayOfMonth(1)