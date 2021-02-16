package com.vitorbasso.gerenciadorinvestimentos.util

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

@Component
object Util {

    fun getAverageCost(value: BigDecimal = BigDecimal.ZERO, quantity: Int = 1): BigDecimal =
        value.divide(BigDecimal(quantity).takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
            ?: BigDecimal.ONE, 20, RoundingMode.HALF_EVEN)

}

fun LocalDateTime.atStartOfMonth(): LocalDate = this.toLocalDate().withDayOfMonth(1)

fun LocalDateTime.atStartOfDay(): LocalDateTime = this.withHour(0).withMinute(0).withSecond(0).withNano(0)