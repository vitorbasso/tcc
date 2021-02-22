package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TaxDeductibleRequest
import java.time.LocalDate

interface ITaxService {
    fun getTax(month: LocalDate): ITax
    fun deduct(taxDeductibleRequest: TaxDeductibleRequest): ITax
}