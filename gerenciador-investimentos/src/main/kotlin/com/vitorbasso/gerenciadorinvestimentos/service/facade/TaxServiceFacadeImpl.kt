package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class TaxServiceFacadeImpl(
    private val taxService: TaxService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    @Qualifier("monthlyWalletServiceFacadeImpl")
    private val monthlyWalletService: IMonthlyWalletService,
) : ITaxService {

    override fun getTax(month: LocalDate) = this.taxService.calculateTax(
        wallets = getTaxables(month)
    )

    private fun getTaxables(month: LocalDate) = (
        (this.monthlyWalletService.getMonthlyWallets().plus(this.walletService.getWallet())
            ) as List<ITaxable>).filter { it.walletMonth.isBefore(month.withDayOfMonth(2)) }

}