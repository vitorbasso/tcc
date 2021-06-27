package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
internal class AssetServiceFacadeImpl(
    private val assetService: AssetService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    private val stockService: IStockService,
    private val context: ApplicationContext
) : IAssetService {

    override fun getAsset(wallet: Wallet, ticker: String) =
        this.assetService.getAssetNullable(wallet, ticker) ?: Asset(
            wallet = wallet,
            stock = this.stockService.getStock(ticker)
        ).let { this.assetService.saveAsset(it) }

    fun reprocessAsset(client: Client, ticker: String, transactions: List<Transaction>) {
        val assets = this.assetService.findAllByClientAndTicker(client, ticker).mapByDate()
        val transactionsByDate = transactions.byDate()
        var lastAsset = Asset()
        var quantityForAverage = 0
        var quantityForAverageDayTrade = 0
        var valueForAverage = BigDecimal.ZERO
        var quantity = 0
        var value = BigDecimal.ZERO
        var inverted = false
        assets.entries.sortedBy { it.key }.map { (month, asset) ->
            val assetTransactions = transactionsByDate[month] ?: listOf()
            transactions.forEach {
                if(it.type == TransactionType.BUY) {
                    valueForAverage = valueForAverage.plus(it.value)
                    quantityForAverage += it.quantity
                    quantity += it.quantity
                    value += it.value
                } else if (it.type == TransactionType.SELL) {
                    quantity -= it.quantity
                    value -= it.value
                }
            }
        }
    }

    @Transactional
    override fun deleteAsset(walletId: Long, ticker: String) {
        val asset = this.assetService.getAsset(
            wallet = this.walletService.getWallet() as Wallet,
            ticker = ticker
        )
//        (context.getBean("accountingService", AccountingService::class) as AccountingService).accountFor(
//            transaction = Transaction(asset = asset),
//            staleTransactions = (context.getBean(
//                "transactionService",
//                TransactionService::class
//            ) as TransactionService).findAllByAsset(asset),
//            accountingOperation = AccountingOperation.REMOVE_ASSET
//        )
        this.assetService.deleteAsset(asset)
    }

    private fun List<Asset>.mapByDate() = this.map { it.wallet.walletMonth to it }.toMap()

    private fun List<Transaction>.byDate() = this.map { it.transactionDate.atStartOfMonth() to it }
        .fold(mutableMapOf<LocalDate, List<Transaction>>()) { result, pair ->
            result[pair.first] = result[pair.first]?.plus(pair.second) ?: listOf(pair.second)
            result
        }.toMap()


}