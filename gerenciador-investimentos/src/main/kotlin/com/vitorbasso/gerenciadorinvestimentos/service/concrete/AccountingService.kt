package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.AccountingOperation
import com.vitorbasso.gerenciadorinvestimentos.service.IAccountingServiceSubscriber
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
internal class AccountingService(
    private val subscribers: List<IAccountingServiceSubscriber>
) {

    fun accountFor(
        transaction: Transaction,
        staleTransactions: List<Transaction>,
        accountingOperation: AccountingOperation = AccountingOperation.ADD_TRANSACTION
    ){}
//    ) = getAccountantReport(transaction, staleTransactions, accountingOperation).also {
//        callSubscribers(transaction, it)
//    }

//    private fun callSubscribers(newTransaction: Transaction, accountantReport: AccountantReport) =
//        subscribers.forEach {
//            it.processAccountantReport(newTransaction, accountantReport)
//        }
//
//    private fun getAccountantReport(
//        transaction: Transaction,
//        staleTransactions: List<Transaction>,
//        accountingOperation: AccountingOperation
//    ): AccountantReport {
//        val accountedFor = mutableListOf<Transaction>()
//
//        return AccountantReport(
//            walletsReport = getWalletsReport(
//                transaction = transaction,
//                staleTransactions = staleTransactions,
//                accountedFor = accountedFor,
//                accountingOperation = accountingOperation
//            ),
//            transactionsReport = accountedFor,
//            assetReport = getAssetReport(accountedFor),
//            lifetimeBalanceChange = when (transaction.type) {
//                TransactionType.BUY -> transaction.value.negate()
//                TransactionType.SELL -> transaction.value
//            }.multiply(BigDecimal(accountingOperation.multiplier)),
//            accountingOperation = accountingOperation
//        )
//    }
//
//    private fun getWalletsReport(
//        transaction: Transaction,
//        staleTransactions: List<Transaction>,
//        accountedFor: MutableList<Transaction>,
//        accountingOperation: AccountingOperation
//    ): Map<LocalDate, WalletReport> {
//        if (accountingOperation == AccountingOperation.REMOVE_ASSET) return calculateContributionDelta(
//            staleTransactions.toMonthMap().mapValues { WalletReport() },
//            calculateContribution(
//                transactions = staleTransactions,
//                daytradeTransactions = staleTransactions.filter { it.daytradeQuantity > 0 },
//                accountingOperation = accountingOperation
//            )
//        )
//
//        val (sameDayTransactions, otherDayTransactions)
//            = staleTransactions.partitionByDate(transaction.transactionDate)
//
//        val beforeOperation = calculateContribution(
//            transactions = staleTransactions,
//            daytradeTransactions = sameDayTransactions,
//            accountingOperation = accountingOperation
//        )
//
//        val daytradeProcessed = when (accountingOperation) {
//            AccountingOperation.ADD_TRANSACTION -> DaytradeService.processDaytrade(
//                (sameDayTransactions + transaction).sortedBy { it.transactionDate }
//            )
//            AccountingOperation.REMOVE_TRANSACTION -> DaytradeService.processDaytrade(
//                sameDayTransactions.filterNot { it == transaction }
//            )
//            AccountingOperation.REMOVE_ASSET -> throw CustomManagerException()
//        }
//
//        val afterOperation = calculateContribution(
//            transactions = (daytradeProcessed + otherDayTransactions).sortedBy { it.transactionDate },
//            daytradeTransactions = daytradeProcessed,
//            accountedFor = accountedFor,
//            accountingOperation = accountingOperation
//        )
//        return calculateContributionDelta(afterOperation, beforeOperation)
//    }
//
//    private fun calculateContributionDelta(
//        afterNewTransactionContribution: Map<LocalDate, WalletReport>,
//        beforeNewTransactionContribution: Map<LocalDate, WalletReport>
//    ) = afterNewTransactionContribution.map {
//        it.key to it.value.subtract(beforeNewTransactionContribution[it.key])
//    }.toMap()
//
//    private fun getAssetReport(accountedFor: MutableList<Transaction>) = accountedFor.lastOrNull()?.let {
//        val normalQuantity = getTransactionNormalQuantity(it)
//        when (it.type) {
//            TransactionType.BUY -> Util.getAverageCost(
//                it.checkingValue.add(
//                    (Util.getAverageCost(it.value, it.quantity).multiply(
//                        BigDecimal(normalQuantity)
//                    ))
//                ), it.checkingQuantity + normalQuantity
//            )
//            TransactionType.SELL ->
//                if (it.checkingQuantity - normalQuantity <= 0) BigDecimal.ZERO
//                else Util.getAverageCost(it.checkingValue, it.checkingQuantity)
//        }
//    } ?: BigDecimal.ZERO
//
//    private fun calculateContribution(
//        transactions: List<Transaction>,
//        daytradeTransactions: List<Transaction>,
//        accountedFor: MutableList<Transaction> = mutableListOf(),
//        accountingOperation: AccountingOperation = AccountingOperation.ADD_TRANSACTION
//    ): Map<LocalDate, WalletReport> {
//        val transactionsByMonth = transactions.toMonthMap()
//
//        val accountantNotes = AccountantNotes(
//            quantityForAverage = transactions.firstOrNull()?.checkingQuantity ?: 0,
//            valueForAverage = transactions.firstOrNull()?.checkingValue ?: BigDecimal.ZERO,
//            quantityCount = transactions.firstOrNull()?.checkingQuantity ?: 0
//        )
//
//        return transactionsByMonth.map { map ->
//            map.key to map.value.let { transactionList ->
//                accountantNotes.balanceContribution = BigDecimal.ZERO
//                accountantNotes.withdrawnContribution = BigDecimal.ZERO
//                transactionList.forEach {
//                    val transaction = it.copy(
//                        checkingQuantity = accountantNotes.quantityForAverage,
//                        checkingValue = accountantNotes.valueForAverage
//                    )
//                    accountedFor.add(transaction)
//                    calculateIndividualTransactionContribution(
//                        transaction = transaction,
//                        accountantNotes = accountantNotes
//                    )
//                }
//
//                val daytradeContribution = DaytradeService.calculateDaytradeContribution(
//                    map.key,
//                    if (accountingOperation != AccountingOperation.REMOVE_ASSET) daytradeTransactions
//                    else daytradeTransactions.toMonthMap()[map.key] ?: listOf()
//                )
//
//                WalletReport(
//                    balanceContribution = accountantNotes.balanceContribution,
//                    withdrawnContribution = accountantNotes.withdrawnContribution,
//                    daytradeBalanceContribution = daytradeContribution.balanceContribution,
//                    daytradeWithdrawnContribution = daytradeContribution.withdrawnContribution
//                )
//            }
//        }.toMap()
//    }
//
//    private fun calculateIndividualTransactionContribution(
//        transaction: Transaction,
//        accountantNotes: AccountantNotes
//    ) {
//        when (transaction.type) {
//            TransactionType.BUY -> processBuyTransactionContribution(transaction, accountantNotes)
//            TransactionType.SELL -> processSellTransactionContribution(transaction, accountantNotes)
//        }
//        if (accountantNotes.quantityCount == 0) {
//            accountantNotes.valueForAverage = BigDecimal.ZERO
//            accountantNotes.quantityForAverage = 0
//        }
//    }
//
//    private fun processBuyTransactionContribution(
//        transaction: Transaction,
//        accountantNotes: AccountantNotes
//    ) {
//        val normalQuantity = getTransactionNormalQuantity(transaction)
//        when {
//            //If it has a positive or null quantity of the asset (meaning if bought more than sold) than it contributes
//            //to the average value of said asset
//            accountantNotes.quantityCount >= 0 -> {
//                accountantNotes.valueForAverage = accountantNotes.valueForAverage.add(
//                    Util.getAverageCost(transaction.value, transaction.quantity).multiply(
//                        BigDecimal(normalQuantity)
//                    )
//                )
//                accountantNotes.quantityForAverage += normalQuantity
//            }
//            //Else if it has a negative quantity of the asset and it is being bought more than that quantity, then it
//            //contributes to the balance corresponding to the negative quantity that it had before and it starts a new
//            //average value corresponding to the now positive quantity of the asset
//            abs(accountantNotes.quantityCount) < normalQuantity -> {
//                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
//                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
//                        Util.getAverageCost(
//                            accountantNotes.valueForAverage,
//                            accountantNotes.quantityForAverage
//                        ).abs()
//                    ).multiply(BigDecimal(accountantNotes.quantityCount))
//                )
//                accountantNotes.quantityForAverage = normalQuantity - abs(accountantNotes.quantityCount)
//                accountantNotes.valueForAverage =
//                    Util.getAverageCost(
//                        transaction.value,
//                        transaction.quantity
//                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
//            }
//            //Finally, if it has a negative quantity of the asset and it is not being bought more than that quantity,
//            //then it just contributes to the balance corresponding to the quantity being bought
//            else -> {
//                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
//                    Util.getAverageCost(accountantNotes.valueForAverage, accountantNotes.quantityForAverage)
//                        .abs()
//                        .subtract(Util.getAverageCost(transaction.value, transaction.quantity))
//                        .multiply(BigDecimal(normalQuantity))
//                )
//            }
//        }
//        accountantNotes.quantityCount += normalQuantity
//    }
//
//    private fun processSellTransactionContribution(
//        transaction: Transaction,
//        accountantNotes: AccountantNotes
//    ) {
//        val normalQuantity = getTransactionNormalQuantity(transaction)
//        accountantNotes.withdrawnContribution = accountantNotes.withdrawnContribution.add(
//            Util.getAverageCost(transaction.value, transaction.quantity).multiply(
//                BigDecimal(normalQuantity)
//            )
//        )
//        //The idea here is mirrored the processBuyTransactionContribution() method on the asset quantities.
//        when {
//            accountantNotes.quantityCount <= 0 -> {
//                accountantNotes.valueForAverage = accountantNotes.valueForAverage.subtract(
//                    Util.getAverageCost(transaction.value, transaction.quantity).multiply(
//                        BigDecimal(normalQuantity)
//                    )
//                )
//                accountantNotes.quantityForAverage -= normalQuantity
//            }
//            accountantNotes.quantityCount < normalQuantity -> {
//                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
//                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
//                        Util.getAverageCost(
//                            accountantNotes.valueForAverage,
//                            accountantNotes.quantityForAverage
//                        )
//                    ).multiply(BigDecimal(accountantNotes.quantityCount))
//                )
//                accountantNotes.quantityForAverage = accountantNotes.quantityCount - normalQuantity
//                accountantNotes.valueForAverage =
//                    Util.getAverageCost(
//                        transaction.value,
//                        transaction.quantity
//                    ).multiply(BigDecimal(accountantNotes.quantityForAverage))
//            }
//            else -> {
//                accountantNotes.balanceContribution = accountantNotes.balanceContribution.add(
//                    Util.getAverageCost(transaction.value, transaction.quantity).subtract(
//                        Util.getAverageCost(
//                            accountantNotes.valueForAverage,
//                            accountantNotes.quantityForAverage
//                        )
//                    ).multiply(BigDecimal(normalQuantity))
//                )
//            }
//        }
//        accountantNotes.quantityCount -= normalQuantity
//    }

    private fun getTransactionNormalQuantity(transaction: Transaction) =
        transaction.quantity - transaction.daytradeQuantity

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
        val lifetimeBalanceChange: BigDecimal = BigDecimal.ZERO,
        val accountingOperation: AccountingOperation = AccountingOperation.ADD_TRANSACTION
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

private fun List<Transaction>.partitionByDate(date: LocalDateTime) =
    this.partition { it.transactionDate.toLocalDate().isEqual(date.toLocalDate()) }

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