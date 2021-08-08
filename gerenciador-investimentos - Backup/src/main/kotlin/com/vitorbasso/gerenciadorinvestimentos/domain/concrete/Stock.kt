package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Stock(
    @Id
    val ticker: String = "",
    val currentValue: BigDecimal = BigDecimal.ZERO,
    val openingValue: BigDecimal = BigDecimal.ZERO,
    val closingValue: BigDecimal = BigDecimal.ZERO,
    val highestValue: BigDecimal = BigDecimal.ZERO,
    val lowestValue: BigDecimal = BigDecimal.ZERO,
    val variation: BigDecimal = BigDecimal.ZERO,
    @field:UpdateTimestamp
    @Column(nullable = false)
    val dateUpdated: LocalDateTime = LocalDateTime.now()
) : Serializable