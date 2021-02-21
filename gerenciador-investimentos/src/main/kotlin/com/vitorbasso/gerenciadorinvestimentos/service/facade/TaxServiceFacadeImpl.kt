package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class TaxServiceFacadeImpl(
    private val taxService: TaxService
) : ITaxService {

    override fun getTax(month: LocalDate) = this.taxService.getTax(month, SecurityContextUtil.getClientDetails())

}