package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.response.AssetDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletDto
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.setScale
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class WalletServiceProxyImpl(
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService
) : IWalletService {

    override fun getWallet() = this.walletService.getWallet().getDto()

    override fun getAllWallets() = this.walletService.getAllWallets().map { it.getDto() }

}

private fun IWallet.getDto() = WalletDto(
    id = (this as Wallet).id,
    withdrawn = this.withdrawn.setScale(),
    balance = this.balance,
    balanceDaytrade = this.balanceDaytrade,
    withdrawnDaytrade = this.withdrawnDaytrade.setScale(),
    walletMonth = this.walletMonth,
    stockAsset = this.asset.map { it.getDto() }
)

private fun IAsset.getDto() = AssetDto(
    id = (this as Asset).id,
    stockSymbol = this.stock.ticker,
    averageCost = this.averageCost.setScale(),
    amount = this.amount,
    balance = this.balance
)