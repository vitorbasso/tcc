package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionDto(
    val id: Long,
    val type: String,
    val quantity: Int,
    val value: BigDecimal,
    val ticker: String,
    val transactionDate: LocalDateTime,
    val daytrade: Boolean,
    val daytradeQuantity: Int
) : ITransaction