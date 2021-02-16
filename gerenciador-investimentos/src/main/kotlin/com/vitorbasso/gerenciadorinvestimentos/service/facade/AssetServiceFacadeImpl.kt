package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IAccountingServiceSubscriber
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
internal class AssetServiceFacadeImpl(
    private val assetService: AssetService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    private val stockService: IStockService
) : IAssetService, IAccountingServiceSubscriber {

    override fun getAsset(wallet: Wallet, stock: Stock) =
        this.assetService.getAssetNullable(wallet, stock) ?: Asset(
            wallet = wallet,
            stock = stock
        ).let { this.assetService.saveAsset(it) }

    override fun deleteAsset(walletId: Long, ticker: String) = this.assetService.deleteAsset(
        asset = this.assetService.getAsset(
            wallet = this.walletService.getWallet(
                walletId = walletId
            ) as Wallet,
            stock = this.stockService.getStock(ticker)
        )
    )

    override fun processAccountantReport(
        transaction: Transaction,
        accountantReport: AccountingService.AccountantReport
    ): AccountingService.AccountantReport {
        this.assetService.saveAsset(
            transaction.asset.copy(
                amount = transaction.asset.amount + when (transaction.type) {
                    TransactionType.BUY -> transaction.quantity
                    TransactionType.SELL -> (-transaction.quantity)
                }.times(accountantReport.accountingOperation.multiplier),
                averageCost = accountantReport.assetReport,
                lifetimeBalance = transaction.asset.lifetimeBalance.add(accountantReport.lifetimeBalanceChange)
            )
        )
        return accountantReport
    }

}