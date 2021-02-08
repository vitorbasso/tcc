package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.util.AccountantUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
internal class AssetServiceFacadeImpl(
    private val assetService: AssetService,
    private val walletService: WalletServiceFacadeImpl,
    private val stockService: StockService
) : IAssetService {

    override fun addTransactionToAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal,
        type: TransactionType
    ) = (this.assetService.getAssetNullable(wallet, stock).let {
        when (type) {
            TransactionType.BUY -> this.assetService.processBuyTransaction(
                asset = it,
                amount = amount,
                cost = cost,
                wallet = wallet,
                stock = stock
            )
            TransactionType.SELL -> this.assetService.processSellTransaction(
                asset = it,
                amount = amount,
                cost = cost,
                wallet = wallet,
                stock = stock
            )
        }
    }).let { this.assetService.saveAsset(it) }

    fun processAssetReport(asset: Asset, accountantReport: AccountantUtil.AccountantReport) =
        this.assetService.saveAsset(
            asset.copy(
                averageCost = accountantReport.assetReport,
                lifetimeBalance = asset.lifetimeBalance.add(accountantReport.lifetimeBalanceChange)
            )
        )

    override fun deleteAsset(walletId: Long, ticker: String) = this.assetService.deleteAsset(
        asset = this.assetService.getAsset(
            wallet = this.walletService.getWallet(
                walletId = walletId
            ),
            stock = this.stockService.getStock(ticker)
        )
    )

}