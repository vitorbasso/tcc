package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.response.MonthlyWalletDto
import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.setScale
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
internal class MonthlyWalletServiceProxyImpl(
    @Qualifier("monthlyWalletServiceFacadeImpl")
    private val monthlyWalletService: IMonthlyWalletService
) : IMonthlyWalletService {

    override fun getMonthlyWallets() = this.monthlyWalletService.getMonthlyWallets().map { it.getDto() }

}

private fun IMonthlyWallet.getDto() = MonthlyWalletDto(
    id = (this as MonthlyWallet).id,
    balance = this.balance,
    balanceDaytrade = this.balanceDaytrade,
    withdrawn = this.withdrawn.setScale(),
    withdrawnDaytrade = this.withdrawnDaytrade.setScale(),
    walletId = this.walletId,
    walletMonth = this.walletMonth
)