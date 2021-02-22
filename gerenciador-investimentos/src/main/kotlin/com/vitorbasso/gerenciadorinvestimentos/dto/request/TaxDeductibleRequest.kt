package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.Min

data class TaxDeductibleRequest(
    @field:Min(value = 0, message = "error.request.body.fields.tax-deductible.deducted")
    val deducted: BigDecimal = BigDecimal.ZERO,
    @field:Min(value = 0, message = "error.request.body.fields.tax-deductible.daytrade-deducted")
    val daytradeDeducted: BigDecimal = BigDecimal.ZERO,
    val month: LocalDate = LocalDate.now().atStartOfMonth()
)