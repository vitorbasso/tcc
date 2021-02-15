package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IAccountingServiceSubscriber
import com.vitorbasso.gerenciadorinvestimentos.util.Util
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.abs

@Service
class AccountingService(
    private val subscribers: List<IAccountingServiceSubscriber>
) {

    fun account(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>
    ) = getAccountantReport(newTransaction, staleTransactions).also { accountantReport ->
        subscribers.forEach {
            it.processAccountantReport(newTransaction, accountantReport)
        }
    }

    private fun getAccountantReport(
        newTransaction: Transaction,
        staleTransactions: List<Transaction>
    ): AccountantReport {
        val accountedFor = mutableListOf<Transaction>()

        return AccountantReport(
            walletsReport = getWalletsReport(
                staleTransactions = staleTransactions,
                newTransaction = newTransaction,
                accountedFor = accountedFor
            ),
            transactionsReport = accountedFor,
            assetReport = getAssetReport(accountedFor),
            lifetimeBalanceChange = when (newTransaction.type) {
                TransactionType.BUY -> newTransaction.value.negate()
                TransactionType.SELL -> newTransaction.value
            }
        )
    }

    private fun getWalletsReport(
        staleTransactions: List<Transaction>,
        newTransaction: Transaction,
        accountedFor: MutableList<Transaction>
    ): Map<LocalDate, WalletReport> {
        val (sameDayTransactions, otherDayTransactions)
            = staleTransactions.partitionByDate(newTransaction.transactionDate.toLocalDate())

        val beforeNewTransactionContribution = calculateContribution(staleTransactions, sameDayTransactions)

        val daytradeProcessed = DaytradeService.processDaytrade(
            (sameDayTransactions + newTransaction).sortedBy { it.transactionDate }
        )

        val afterNewTransactionContribution = calculateContribution(
            transactions = (daytradeProcessed + otherDayTransactions).sortedBy { it.transactionDate },
            daytradeTransactions = daytradeProcessed,
            accountedFor = accountedFor
        )
        return calculateContributionDelta(afterNewTransactionContribution, beforeNewTransactionContribution)
    }

    private fun calculateContributionDelta(
        afterNewTransactionContribution: Map<LocalDate, WalletReport>,
        beforeNewTransactionContribution: Map<LocalDate, WalletReport>
    ) = afterNewTransactionContribution.map {
        it.key to it.value.subtract(beforeNewTransactionContribution[it.key])
    }.toMap()

    private fun getAssetReport(accountedFor: MutableList<Transaction>) = accountedFor.last().let {
        when (it.type) {
            TransactionType.BUY -> Util.getAverageCost(
                it.checkingValue.add(
                    (Util.getAverageCost(it.value, it.quantity).multiply(
                        BigDecimal(it.quantity - it.daytradeQuantity)
                    ))
                ), it.checkingQuantity + (it.quantity - it.daytradeQuantity)
            )
            TransactionType.SELL ->
                if (it.checkingQuantity - (it.quantity - it.daytradeQuantity) <= 0) BigDecimal.ZERO
                else Util.getAverageCost(it.checkingValue, it.checkingQuantity)
        }
    }

    private fun calculateContribution(
        transactions: List<Transaction>,
        daytradeTransactions: List<Transaction>,
        accountedFor: MutableList<Transaction> = mutableListOf()
    ): Map<LocalDate, WalletReport> {
        val transactionsByMonth = transactions.toMonthMap()

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
                    calculateIndividualTransactionContribution(
                        transaction = transaction,
                        accountantNotes = accountantNotes
                    )
                }

                val daytradeContribution = DaytradeService.calculateDaytradeContribution(map.key, daytradeTransactions)

                WalletReport(
                    balanceContribution = accountantNotes.balanceContribution,
                    withdrawnContribution = accountantNotes.withdrawnContribution,
                    daytradeBalanceContribution = daytradeContribution.balanceContribution,
                    daytradeWithdrawnContribution = daytradeContribution.withdrawnContribution
                )
            }
        }.toMap()
    }

    private fun calculateIndividualTransactionContribution(
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
                    Util.getAverageCost(transaction.value, transaction.quantity).multiply(
                        BigDecimal(normalQuantity)
                    )
                )
                accountantNotes.quantityForAverage += normalQuantity
            }
            abs(accountantNotes.quantityCount) < normalQuantity -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
                        Util.getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        ).abs()
                    ).multiply(BigDecimal(accountantNotes.quantityCount))
                )
                accountantNotes.quantityForAverage = normalQuantity - abs(accountantNotes.quantityCount)
                accountantNotes.valueForAverage =
                    Util.getAverageCost(
                        transaction.value,
                        transaction.quantity
                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
            }
            else -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    Util.getAverageCost(accountantNotes.valueForAverage, accountantNotes.quantityForAverage)
                        .abs()
                        .subtract(Util.getAverageCost(transaction.value, transaction.quantity))
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
            Util.getAverageCost(transaction.value, transaction.quantity).multiply(
                BigDecimal(normalQuantity)
            )
        )
        when {
            accountantNotes.quantityCount <= 0 -> {
                accountantNotes.valueForAverage = accountantNotes.valueForAverage.subtract(
                    Util.getAverageCost(transaction.value, transaction.quantity).multiply(
                        BigDecimal(normalQuantity)
                    )
                )
                accountantNotes.quantityForAverage -= normalQuantity
            }
            accountantNotes.quantityCount < normalQuantity -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
                        Util.getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        )
                    ).multiply(BigDecimal(accountantNotes.quantityCount))
                )
                accountantNotes.quantityForAverage = accountantNotes.quantityCount - normalQuantity
                accountantNotes.valueForAverage =
                    Util.getAverageCost(
                        transaction.value,
                        transaction.quantity
                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
            }
            else -> {
                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
                        Util.getAverageCost(
                            accountantNotes.valueForAverage,
                            accountantNotes.quantityForAverage
                        )
                    ).multiply(BigDecimal(normalQuantity))
                )
            }
        }
        accountantNotes.quantityCount -= normalQuantity
    }

    data class WalletReport(
        val balanceContribution: BigDecimal = BigDecimal.ZERO,
        val daytradeBalanceContribution: BigDecimal = BigDecimal.ZERO,
        val withdrawnContribution: BigDecimal = BigDecimal.ZERO,
        val daytradeWithdrawnContribution: BigDecimal = BigDecimal.ZERO
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

}

private fun List<Transaction>.toMonthMap() =
    mutableMapOf<LocalDate, List<Transaction>>().let { map ->
        this.map { it.transactionDate.atStartOfMonth() to it }.forEach {
            map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
        }
        map
    }

private fun List<Transaction>.partitionByDate(date: LocalDate) =
    this.partition { it.transactionDate.toLocalDate().isEqual(date) }

private fun AccountingService.WalletReport.subtract(other: AccountingService.WalletReport?) =
    AccountingService.WalletReport(
        balanceContribution = this.balanceContribution.subtract(other?.balanceContribution ?: BigDecimal.ZERO),
        daytradeBalanceContribution = this.daytradeBalanceContribution.subtract(
            other?.daytradeBalanceContribution ?: BigDecimal.ZERO
        ),
        withdrawnContribution = this.withdrawnContribution.subtract(other?.withdrawnContribution ?: BigDecimal.ZERO),
        daytradeWithdrawnContribution = this.daytradeWithdrawnContribution.subtract(
            other?.daytradeWithdrawnContribution ?: BigDecimal.ZERO
        ),
    )