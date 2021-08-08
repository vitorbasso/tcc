package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import org.hibernate.Hibernate
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
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Stock

        return ticker == other.ticker
    }

    override fun hashCode(): Int = 1136165790

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(ticker = $ticker , currentValue = $currentValue , " +
            "openingValue = $openingValue , closingValue = $closingValue , highestValue = $highestValue , " +
            "lowestValue = $lowestValue , variation = $variation , dateUpdated = $dateUpdated )"
    }
}