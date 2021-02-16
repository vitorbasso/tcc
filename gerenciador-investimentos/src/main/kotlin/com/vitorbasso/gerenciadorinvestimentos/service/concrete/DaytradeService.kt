package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.util.Util
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
object DaytradeService {

    fun calculateDaytradeContribution(
        month: LocalDate,
        transactions: List<Transaction>
    ): DaytradeContribution {
        if (!isDaytradeMonth(month, transactions))
            return DaytradeContribution()

        val (buyTransactions, sellTransactions) = transactions.partition {
            it.type == TransactionType.BUY
        }
        val buyValue = buyTransactions.fold(BigDecimal.ZERO) { total, transaction ->
            total + Util.getAverageCost(
                transaction.value,
                transaction.quantity
            ).multiply(BigDecimal(transaction.daytradeQuantity))
        }
        val sellValue = sellTransactions.fold(BigDecimal.ZERO) { total, transaction ->
            total + Util.getAverageCost(
                transaction.value,
                transaction.quantity
            ).multiply(BigDecimal(transaction.daytradeQuantity))
        }
        return DaytradeContribution(
            sellValue.subtract(buyValue),
            sellValue
        )
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

    private fun getBuyAndSellLists(sameDayTransactions: List<Transaction>) = (sameDayTransactions.map {
        it.copy(
            daytrade = false,
            daytradeQuantity = 0
        )
    }.partition {
        it.type == TransactionType.BUY
    }).let { Pair(it.first.toMutableList(), it.second.toMutableList()) }

    private fun isDaytradeMonth(
        month: LocalDate,
        daytradeTransactions: List<Transaction>
    ) = month.isEqual(
        daytradeTransactions.firstOrNull()?.transactionDate?.atStartOfMonth() ?: month.withDayOfMonth(2)
    )

    data class DaytradeContribution(
        val balanceContribution: BigDecimal = BigDecimal.ZERO,
        val withdrawnContribution: BigDecimal = BigDecimal.ZERO
    )

}