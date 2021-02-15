package com.vitorbasso.gerenciadorinvestimentos.util

import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Component
object Util {

    fun getAverageCost(value: BigDecimal = BigDecimal.ZERO, quantity: Int = 1) =
        value.divide(BigDecimal(quantity).takeIf { it.compareTo(BigDecimal.ZERO) != 0 }
            ?: BigDecimal.ONE, 20, RoundingMode.HALF_EVEN)

}

fun LocalDateTime.atStartOfMonth() = this.toLocalDate().withDayOfMonth(1)

fun LocalDateTime.atStartOfDay() = this.withHour(0).withMinute(0).withSecond(0).withNano(0)