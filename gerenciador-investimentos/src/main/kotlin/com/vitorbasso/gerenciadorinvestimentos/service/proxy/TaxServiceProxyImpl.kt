package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import com.vitorbasso.gerenciadorinvestimentos.dto.response.TaxDto
import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import com.vitorbasso.gerenciadorinvestimentos.util.setScale
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Primary
class TaxServiceProxyImpl(
    @Qualifier("taxServiceFacadeImpl")
    private val taxService: ITaxService
) : ITaxService {

    override fun getTax(month: LocalDate) = this.taxService.getTax(month).toDto()

}

private fun ITax.toDto() = TaxDto(
    normalTax = (this as TaxService.TaxInfo).normalTax.setScale(),
    daytradeTax = this.daytradeTax.setScale(),
    availableToDeduct = this.availableToDeduct.setScale(),
    daytradeAvailableToDeduct = this.daytradeAvailableToDeduct.setScale(),
    deducted = this.deducted.setScale(),
    daytradeDeducted = this.daytradeDeducted.setScale(),
    baseForCalculation = this.balance.setScale(),
    daytradeBaseForCalculation = this.daytradeBalance.setScale(),
    withdrawn = this.withdrawn.setScale(),
    daytradeWithdrawn = this.daytradeWithdrawn.setScale()
)