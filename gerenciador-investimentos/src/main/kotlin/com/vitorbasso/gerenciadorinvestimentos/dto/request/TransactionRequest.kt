package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class TransactionRequest(
    @field:Min(value = 0, message = "error.request.body.fields.transaction.quantity")
    val quantity: Int,
    @field:Min(value = 0, message = "error.request.body.fields.transaction.value")
    val value: BigDecimal,
    @field:NotBlank(message = "error.request.body.fields.transaction.type")
    val type: TransactionType,
    @field:NotBlank(message = "error.request.body.fields.transaction.ticker")
    val ticker: String,
    @field:NotBlank(message = "error.request.body.fields.transaction.walletId")
    val walletId: Long,
    val date: LocalDate = LocalDate.now()
)