package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import java.math.BigDecimal

data class TaxDto(
    val normalTax: BigDecimal = BigDecimal.ZERO,
    val baseForCalculation: BigDecimal = BigDecimal.ZERO,
    val withdrawn: BigDecimal = BigDecimal.ZERO,
    val daytradeWithdrawn: BigDecimal = BigDecimal.ZERO,
    val daytradeTax: BigDecimal = BigDecimal.ZERO,
    val daytradeBaseForCalculation: BigDecimal = BigDecimal.ZERO,
    val availableToDeduct: BigDecimal = BigDecimal.ZERO,
    val daytradeAvailableToDeduct: BigDecimal = BigDecimal.ZERO,
    val deducted: BigDecimal = BigDecimal.ZERO,
    val daytradeDeducted: BigDecimal = BigDecimal.ZERO
) : ITax