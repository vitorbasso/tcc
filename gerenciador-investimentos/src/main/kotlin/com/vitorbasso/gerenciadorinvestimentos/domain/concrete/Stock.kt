package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
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
        @field:UpdateTimestamp
        @Column(nullable = false)
        val dateUpdated: LocalDateTime = LocalDateTime.now()
) : Serializable