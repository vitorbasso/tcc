package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Stock (
        @Id
        val ticker: String,
        val currentValue: BigDecimal,
        val openingValue: BigDecimal,
        val closingValue: BigDecimal,
        val highestValue: BigDecimal,
        val lowestValue: BigDecimal,
        val variation: BigDecimal,
        val marketValue: BigDecimal,
        val dailyVolume: Long,
        val paperInCirculation: Long
) : Serializable