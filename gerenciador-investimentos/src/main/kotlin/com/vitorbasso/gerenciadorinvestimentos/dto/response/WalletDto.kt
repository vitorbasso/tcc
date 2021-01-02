package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet

data class WalletDto (
    val id: Long,
    val name: String,
    val broker: String,
    val monthlyBalanceDaytrade: java.math.BigDecimal,
    val monthlyBalance: java.math.BigDecimal,
    val lifetimeBalanceDaytrade: java.math.BigDecimal,
    val lifetimeBalance: java.math.BigDecimal,
    val stockAsset: List<AssetDto>
) : IWallet