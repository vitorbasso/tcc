package com.vitorbasso.gerenciadorinvestimentos.dto.response

import java.time.LocalDate

data class MonthlyWalletSmallDto(
    val id: Long,
    val name: String,
    val broker: String,
    val month: LocalDate
)