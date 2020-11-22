package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate

@Service
internal class TransactionServiceFacadeImpl(
    private val transactionService: TransactionService,
    private val stockService: StockService,
    private val assetService: IAssetService,
    private val walletService: WalletServiceFacadeImpl
) : ITransactionService {

    @Transactional
    override fun performTransaction(transactionRequest: TransactionRequest)
        = this.assetService.addTransactionToAsset(
        wallet = this.walletService.getWallet(transactionRequest.broker) as Wallet,
        stock = this.stockService.getStock(transactionRequest.ticker),
        amount = transactionRequest.quantity,
        cost = transactionRequest.value,
        type = transactionRequest.type
    ).let {
        transactionRequest.getTransaction(it)
    }.let {
        this.transactionService.save(processTransaction(it))
    }

    private fun TransactionRequest.getTransaction(asset: Asset) = Transaction(
        type = this.type,
        quantity = this.quantity,
        value = this.value,
        asset = asset,
        transactionDate = checkDate(this.date)
    )

    private fun checkDate(dateToCheck: LocalDate) = dateToCheck.takeIf {
        !it.isAfter(LocalDate.now()) && (it.dayOfWeek != DayOfWeek.SATURDAY || it.dayOfWeek != DayOfWeek.SUNDAY)
    } ?: throw CustomWrongDateException()

    private fun processTransaction(transaction: Transaction) : Transaction {
        val processedTransactionForDaytrade = processDaytrade(transaction)
        this.walletService.updateBalance(
            processedTransactionForDaytrade,
            this.transactionService.findTransactionsOnSameMonth(processedTransactionForDaytrade)
        )
        return processedTransactionForDaytrade
    }

    private fun processDaytrade(transaction: Transaction): Transaction {

        val sameDayTransactions = this.transactionService.findTransactionsOnSameDate(transaction)

        var sameTypeTransactionAssetQuantity = -1 * sameDayTransactions.filter {
            it.type == transaction.type && it.daytradeQuantity != it.quantity
        }.fold(0) { total, sameTypeTransaction ->
            total + sameTypeTransaction.quantity
        }

        val otherTypeTransactionList = sameDayTransactions.filter {
            it.type != transaction.type && it.daytradeQuantity != it.quantity
        }

        var quantityStillAvailableForDaytrade = 0

        for (otherTypeTransaction in otherTypeTransactionList) {
            sameTypeTransactionAssetQuantity += otherTypeTransaction.quantity - otherTypeTransaction.daytradeQuantity
            if (sameTypeTransactionAssetQuantity > 0) {
                quantityStillAvailableForDaytrade = updatePastTransactionsForDaytrade(
                    otherTypeTransactions = otherTypeTransactionList.subList(
                        otherTypeTransactionList.indexOf(otherTypeTransaction),
                        otherTypeTransactionList.size
                    ),
                    lastQuantityStillAvailableForDaytrade = sameTypeTransactionAssetQuantity,
                    transactionQuantity = transaction.quantity
                )
                break
            }
        }

        return if (sameTypeTransactionAssetQuantity > 0)
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity - quantityStillAvailableForDaytrade
            )
        else transaction
    }

    private fun updatePastTransactionsForDaytrade(
        otherTypeTransactions: List<Transaction>,
        lastQuantityStillAvailableForDaytrade: Int,
        transactionQuantity: Int
    ): Int {
        var quantityLeftAvailableForDaytrading = transactionQuantity

        otherTypeTransactions.forEachIndexed { index, transaction ->
            if (quantityLeftAvailableForDaytrading > 0) {
                quantityLeftAvailableForDaytrading = updatePastTransaction(
                    transaction = transaction,
                    quantityLeft = quantityLeftAvailableForDaytrading,
                    quantityAvailableToUse =
                    if(index > 0 || lastQuantityStillAvailableForDaytrade <= 0) transaction.quantity
                    else lastQuantityStillAvailableForDaytrade
                )
            }
        }

        return quantityLeftAvailableForDaytrading
    }

    private fun updatePastTransaction(
        transaction: Transaction,
        quantityLeft: Int,
        quantityAvailableToUse: Int
    ) = if (quantityLeft > quantityAvailableToUse) {
        this.transactionService.save(
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity
            )
        )
        quantityLeft - quantityAvailableToUse
    } else {
        this.transactionService.save(
            transaction.copy(
                daytrade = true,
                daytradeQuantity = transaction.quantity - (quantityAvailableToUse - quantityLeft)
            )
        )
        0
    }

}