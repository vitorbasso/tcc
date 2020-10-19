package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionDto(
    val type: String,
    val quantity: Int,
    val value: BigDecimal,
    val ticker: String,
    val transactionDate: LocalDate
) : ITransaction