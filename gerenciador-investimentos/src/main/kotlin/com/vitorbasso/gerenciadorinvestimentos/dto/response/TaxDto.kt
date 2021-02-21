package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import java.math.BigDecimal
import java.time.LocalDate

data class TaxDto(
    val id: Long,
    val tax: BigDecimal,
    val daytradeTax: BigDecimal,
    val deducted: BigDecimal,
    val daytradeDeducted: BigDecimal,
    val availableToDeduct: BigDecimal,
    val daytradeAvailableToDeduct: BigDecimal,
    val month: LocalDate
): ITax