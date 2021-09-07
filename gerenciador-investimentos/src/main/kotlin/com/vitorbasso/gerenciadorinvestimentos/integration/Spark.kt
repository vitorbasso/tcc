package com.vitorbasso.gerenciadorinvestimentos.integration

import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.SparkDto
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfDay
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

data class Spark(
    val symbol: String,
    val lastClose: BigDecimal? = null,
    val lastWeekClose: BigDecimal? = null,
    val lastMonthClose: BigDecimal? = null,
    val lastYearClose: BigDecimal? = null
) {

    companion object {
        fun from(symbol: String, sparkDto: SparkDto): Spark {
            val dates = getDates()
            val datesList = dates.toList()
            val instantMap =
                sparkDto.timestamp.filter {
                    val localDate = LocalDateTime.ofInstant(it, ZoneId.systemDefault()).atStartOfDay()
                    localDate in dates.values
                }.mapNotNull { instant ->
                    val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).atStartOfDay()
                    val date = datesList.find { it.second.isEqual(localDate) }
                    if (date?.first != null)
                        date.first to instant
                    else null
                }.toMap()

            return Spark(
                symbol = symbol,
                lastClose = sparkDto.timestamp.indexOf(instantMap["lastClose"]).takeIf { it != -1 }
                    ?.let { sparkDto.close[it] },
                lastWeekClose = sparkDto.timestamp.indexOf(instantMap["lastWeekClose"]).takeIf { it != -1 }
                    ?.let { sparkDto.close[it] },
                lastMonthClose = sparkDto.timestamp.indexOf(instantMap["lastMonthClose"]).takeIf { it != -1 }
                    ?.let { sparkDto.close[it] },
                lastYearClose = sparkDto.timestamp.indexOf(instantMap["lastYearClose"]).takeIf { it != -1 }
                    ?.let { sparkDto.close[it] },
            )
        }
    }


}

private fun getDates(): Map<String, LocalDateTime> {
    val today = LocalDateTime.now().atStartOfDay()
    val lastClose = when (today.dayOfWeek) {
        DayOfWeek.SUNDAY -> today.minusDays(2)
        DayOfWeek.MONDAY -> today.minusDays(3)
        else -> today.minusDays(1)
    }.checkHoliday()
    val lastWeekClose = today.minusDays(today.dayOfWeek.value.toLong() + 2).checkHoliday()
    val lastMonthClose = today.withDayOfMonth(1).minusDays(1).let {
        when (it.dayOfWeek) {
            DayOfWeek.SATURDAY -> it.minusDays(1)
            DayOfWeek.SUNDAY -> it.minusDays(2)
            else -> it
        }
    }.checkHoliday()
    val lastYearClose = today.withDayOfMonth(1).withMonth(1).minusDays(2).let {
        when (it.dayOfWeek) {
            DayOfWeek.SATURDAY -> it.minusDays(1)
            DayOfWeek.SUNDAY -> it.minusDays(2)
            else -> it
        }
    }.checkHoliday()
    return mapOf(
        "lastClose" to lastClose,
        "lastWeekClose" to lastWeekClose,
        "lastMonthClose" to lastMonthClose,
        "lastYearClose" to lastYearClose
    )
}

private fun LocalDateTime.checkHoliday(): LocalDateTime {
    return when (this.month) {
        Month.DECEMBER -> {
            when (this.dayOfMonth) {
                24 -> this.withDayOfMonth(23)
                31 -> this.withDayOfMonth(30)
                else -> this
            }
        }
        Month.JANUARY -> {
            when (this.dayOfMonth) {
                1 -> this.minusYears(1).withMonth(Month.DECEMBER.value).withDayOfMonth(30)
                else -> this
            }
        }
        else -> this
    }
}