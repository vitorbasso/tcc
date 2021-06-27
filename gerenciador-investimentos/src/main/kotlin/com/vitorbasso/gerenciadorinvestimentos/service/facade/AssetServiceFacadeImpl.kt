package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AssetServiceFacadeImpl(
    private val assetService: AssetService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    private val stockService: IStockService,
    private val context: ApplicationContext
) : IAssetService {

    override fun getAsset(wallet: Wallet, stock: Stock) =
        this.assetService.getAssetNullable(wallet, stock) ?: Asset(
            wallet = wallet,
            stock = stock
        ).let { this.assetService.saveAsset(it) }

    @Transactional
    override fun deleteAsset(walletId: Long, ticker: String) {
        val asset = this.assetService.getAsset(
            wallet = this.walletService.getWallet() as Wallet,
            stock = this.stockService.getStock(ticker)
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

}