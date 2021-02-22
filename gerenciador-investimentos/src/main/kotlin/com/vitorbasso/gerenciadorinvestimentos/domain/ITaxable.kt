package com.vitorbasso.gerenciadorinvestimentos.domain

import java.math.BigDecimal
import java.time.LocalDate

interface ITaxable {
    val balance: BigDecimal
    val balanceDaytrade: BigDecimal
    val withdrawn: BigDecimal
    val withdrawnDaytrade: BigDecimal
    val walletMonth: LocalDate
}