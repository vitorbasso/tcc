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
    val lastClose: BigDecimal? = BigDecimal.ZERO,
    val lastWeekClose: BigDecimal? = BigDecimal.ZERO,
    val lastMonthClose: BigDecimal? = BigDecimal.ZERO,
    val lastYearClose: BigDecimal? = BigDecimal.ZERO,
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

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(ticker = $ticker , currentValue = $currentValue , lastClose = $lastClose , " +
            "lastWeekClose = $lastWeekClose , lastMonthClose = $lastMonthClose , lastYearClose = $lastYearClose , " +
            "dateUpdated = $dateUpdated )"
    }

}