package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet
import java.math.BigDecimal
import java.time.LocalDate

data class MonthlyWalletDto(
    val id: Long,
    val name: String,
    val broker: String,
    val balanceDaytrade: BigDecimal,
    val balance: BigDecimal,
    val walletId: Long,
    val walletMonth: LocalDate
) : IMonthlyWallet