package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionDto(
    val id: Long,
    val type: TransactionType,
    val quantity: Int,
    val value: BigDecimal,
    val transactionDate: LocalDateTime,
) : ITransaction