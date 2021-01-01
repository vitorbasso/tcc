package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.AssetDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletSmallDto
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Primary
class WalletServiceProxyImpl(
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService
) : IWalletService {

    override fun getWalletCollection()
        = this.walletService.getWalletCollection().map { it.getSmallDto() }

    override fun getWallet(walletId: Long) = this.walletService.getWallet(walletId).getDto()

    override fun saveWallet(walletToSave: IWallet)
        = this.walletService.saveWallet(walletToSave.getEntity()).getDto()

    override fun updateWallet(walletId: Long, walletUpdateRequest: WalletUpdateRequest)
        = this.walletService.updateWallet(
        walletId = walletId,
        walletUpdateRequest = walletUpdateRequest
    ).getDto()

    override fun deleteWallet(walletId: Long) {
        this.walletService.deleteWallet(walletId)
    }

}

private fun IWallet.getEntity() = Wallet(
    name = (this as WalletRequest).name,
    broker = this.broker,
    client = SecurityContextHolder.getContext().authentication.principal as Client,
    walletMonth = LocalDate.now()
)

private fun IWallet.getDto() = WalletDto(
    name = (this as Wallet).name,
    broker = this.broker,
    monthlyBalanceDaytrade = this.monthlyBalanceDaytrade,
    monthlyBalance = this.monthlyBalance,
    lifetimeBalanceDaytrade = this.lifetimeBalanceDaytrade,
    lifetimeBalance = this.lifetimeBalance,
    stockAsset = this.asset.map { it.getDto() }
)

private fun IWallet.getSmallDto() = WalletSmallDto(
    name = (this as Wallet).name,
    broker = this.broker
)

private fun IAsset.getDto() = AssetDto(
    stockSymbol = (this as Asset).stock.ticker,
    averageCost = this.averageCost,
    amount = this.amount,
    lifetimeBalance = this.lifetimeBalance
)