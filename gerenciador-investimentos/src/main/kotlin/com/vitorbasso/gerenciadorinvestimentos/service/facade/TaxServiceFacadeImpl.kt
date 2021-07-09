package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.TaxDeductible
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TaxDeductibleRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class TaxServiceFacadeImpl(
    private val taxService: TaxService,
    @Qualifier("walletServiceFacadeImpl")
    private val walletService: IWalletService,
    @Qualifier("clientServiceFacadeImpl")
    private val clientService: IClientService
) : ITaxService {

    override fun getTax(month: LocalDate) = this.taxService.calculateTax(
        month = month,
        wallets = getTaxables(month),
        deductibles = this.taxService.getTaxDeductibles(month, SecurityContextUtil.getClientDetails())
    )

    override fun deduct(taxDeductibleRequest: TaxDeductibleRequest) = this.taxService.deductFromTax(
        tax = getTax(taxDeductibleRequest.month),
        deductible = getTaxDeductible(taxDeductibleRequest.month.atStartOfMonth()),
        deductibleRequest = taxDeductibleRequest
    )

    private fun getTaxDeductible(month: LocalDate) =
        this.taxService.getTaxDeductibles(month, SecurityContextUtil.getClientDetails())
            .find { it.month.equals(month) }
            ?: TaxDeductible(client = SecurityContextUtil.getClientDetails())

    private fun getTaxables(month: LocalDate) = (
        (listOf(this.walletService.getAllWallets())
        ) as List<ITaxable>).filter { it.walletMonth.isBefore(month.withDayOfMonth(2)) }

}