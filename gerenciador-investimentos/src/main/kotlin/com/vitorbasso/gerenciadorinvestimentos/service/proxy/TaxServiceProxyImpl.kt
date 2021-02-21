package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Tax
import com.vitorbasso.gerenciadorinvestimentos.dto.response.TaxDto
import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.math.RoundingMode
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
    id = (this as Tax).id,
    tax = this.tax.setScale(2, RoundingMode.CEILING),
    daytradeTax = this.daytradeTax.setScale(2, RoundingMode.CEILING),
    deducted = this.deducted.setScale(2, RoundingMode.CEILING),
    daytradeDeducted = this.daytradeDeducted.setScale(2, RoundingMode.CEILING),
    availableToDeduct = this.availableToDeduct.setScale(2, RoundingMode.CEILING),
    daytradeAvailableToDeduct = this.daytradeAvailableToDeduct.setScale(2, RoundingMode.CEILING),
    month = this.month
)