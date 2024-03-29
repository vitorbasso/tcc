package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import java.math.BigDecimal
import java.time.LocalDate

data class WalletDto(
    val id: Long,
    val balanceDaytrade: BigDecimal,
    val balance: BigDecimal,
    val withdrawn: BigDecimal,
    val withdrawnDaytrade: BigDecimal,
    val walletMonth: LocalDate,
    val stockAssets: List<AssetWithoutTransactionsDto>
) : IWallet