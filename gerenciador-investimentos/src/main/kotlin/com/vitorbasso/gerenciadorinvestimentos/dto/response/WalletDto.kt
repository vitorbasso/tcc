package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import java.math.BigDecimal

data class WalletDto(
    val id: Long,
    val name: String,
    val broker: String,
    val monthlyBalanceDaytrade: BigDecimal,
    val monthlyBalance: BigDecimal,
    val lifetimeBalanceDaytrade: BigDecimal,
    val lifetimeBalance: BigDecimal,
    val withdrawn: BigDecimal,
    val withdrawnDaytrade: BigDecimal,
    val stockAsset: List<AssetDto>
) : IWallet