package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IStock
import java.math.BigDecimal

data class StockDto(
    val ticker: String,
    val currentValue: BigDecimal,
    val openingValue: BigDecimal,
    val closingValue: BigDecimal,
    val highestValue: BigDecimal,
    val lowestValue: BigDecimal,
    val variation: BigDecimal
) : IStock