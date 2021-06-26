package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.MonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class MonthlyWalletServiceFacadeImpl(
    private val monthlyWalletService: MonthlyWalletService
) : IMonthlyWalletService {

    override fun getMonthlyWallets() =
        this.monthlyWalletService.getMonthlyWallets(SecurityContextUtil.getClientDetails().id)

    fun getMonthlyWalletByMonth(month: LocalDate) = this.monthlyWalletService.getMonthlyWalletByMonth(
        month.atStartOfMonth(),
        SecurityContextUtil.getClientDetails().id
    )

}