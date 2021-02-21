package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.response.MonthlyWalletDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.MonthlyWalletSmallDto
import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
@Primary
internal class MonthlyWalletServiceProxyImpl(
    @Qualifier("monthlyWalletServiceFacadeImpl")
    private val monthlyWalletService: IMonthlyWalletService
) : IMonthlyWalletService {

    override fun getMonthlyWallets() = this.monthlyWalletService.getMonthlyWallets().map { it.getSmallDto() }

    override fun getMonthlyWallet(monthlyWalletId: Long) =
        this.monthlyWalletService.getMonthlyWallet(monthlyWalletId).getDto()

    override fun deleteMonthlyWallet(monthlyWalletId: Long) =
        this.monthlyWalletService.deleteMonthlyWallet(monthlyWalletId)

}

private fun IMonthlyWallet.getDto() = MonthlyWalletDto(
    id = (this as MonthlyWallet).id,
    name = this.name,
    broker = this.broker,
    balanceDaytrade = this.balanceDaytrade.setScale(2, RoundingMode.CEILING),
    balance = this.balance.setScale(2, RoundingMode.CEILING),
    withdrawn = this.withdrawn.setScale(2, RoundingMode.CEILING),
    withdrawnDaytrade = this.withdrawnDaytrade.setScale(2, RoundingMode.CEILING),
    walletId = this.walletId,
    walletMonth = this.walletMonth
)

private fun IMonthlyWallet.getSmallDto() = MonthlyWalletSmallDto(
    id = (this as MonthlyWallet).id,
    name = this.name,
    broker = this.broker,
    month = this.walletMonth
)