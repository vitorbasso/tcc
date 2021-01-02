package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.response.MonthlyWalletDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.MonthlyWalletSmallDto
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.MonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service

@Service
class MonthlyWalletServiceProxy(
    private val monthlyWalletService: MonthlyWalletService
) {

    fun getMonthlyWallets()
    = this.monthlyWalletService.getMonthlyWallets(SecurityContextUtil.getClientDetails().id).map { it.getSmallDto() }

    fun getMonthlyWallet(monthlyWalletId: Long) = this.monthlyWalletService.getMonthlyWallet(monthlyWalletId).getDto()


}

private fun MonthlyWallet.getDto() = MonthlyWalletDto(
    id = this.id,
    name = this.name,
    broker = this.broker,
    monthlyBalanceDaytrade = this.monthlyBalanceDaytrade,
    monthlyBalance = this.monthlyBalance,
    walletId = this.walletId,
    walletMonth = this.walletMonth
)

private fun MonthlyWallet.getSmallDto() = MonthlyWalletSmallDto(
    id = this.id,
    name = this.name,
    broker = this.broker,
    month = this.walletMonth
)