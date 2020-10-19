package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import java.math.BigDecimal

data class TransactionDto(
    val type: String,
    val quantity: Int,
    val value: BigDecimal,
    val ticker: String
) : ITransaction