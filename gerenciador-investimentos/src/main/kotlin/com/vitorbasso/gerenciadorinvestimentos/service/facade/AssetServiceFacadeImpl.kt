package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service

@Service
internal class AssetServiceFacadeImpl (
    private val assetService: AssetService,
    private val walletService: WalletService,
    private val stockService: StockService
) : IAssetService {

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