package com.vitorbasso.gerenciadorinvestimentos.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class SparkDto(
    val timestamp: List<Instant>?,
    val close: List<BigDecimal>?
)