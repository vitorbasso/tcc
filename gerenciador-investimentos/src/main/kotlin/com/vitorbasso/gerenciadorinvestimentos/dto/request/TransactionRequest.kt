package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionRequest(
    val quantity: Int,
    val value: BigDecimal,
    val type: TransactionType,
    val ticker: String,
    val broker: String,
    val date: LocalDate = LocalDate.now()
)