package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.MonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service

@Service
internal class MonthlyWalletServiceFacadeImpl(
    private val monthlyWalletService: MonthlyWalletService
) : IMonthlyWalletService {

    override fun getMonthlyWallets()
    = this.monthlyWalletService.getMonthlyWallets(SecurityContextUtil.getClientDetails().id)

    override fun getMonthlyWallet(monthlyWalletId: Long) = this.monthlyWalletService.getMonthlyWallet(
        monthlyWalletId,
        SecurityContextUtil.getClientDetails().id
    )

    override fun deleteMonthlyWallet(monthlyWalletId: Long) = this.monthlyWalletService.deleteMonthlyWallet(
        this.monthlyWalletService.getMonthlyWallet(
            monthlyWalletId = monthlyWalletId,
            clientId = SecurityContextUtil.getClientDetails().id,
            exception = CustomBadRequestException(ManagerErrorCode.MANAGER_11)
        )
    )

}