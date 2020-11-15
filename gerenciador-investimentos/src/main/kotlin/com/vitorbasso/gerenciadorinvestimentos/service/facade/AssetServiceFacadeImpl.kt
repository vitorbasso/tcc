package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
internal class AssetServiceFacadeImpl (
    private val assetService: AssetService,
    private val walletService: WalletService,
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

    override fun deleteAsset(broker: String, ticker: String) = this.assetService.deleteAsset(
        asset = this.assetService.getAsset(
            wallet = this.walletService.getWallet(
                client = SecurityContextUtil.getClientDetails(),
                broker = broker
            ),
            stock = this.stockService.getStock(ticker)
        )
    )

}