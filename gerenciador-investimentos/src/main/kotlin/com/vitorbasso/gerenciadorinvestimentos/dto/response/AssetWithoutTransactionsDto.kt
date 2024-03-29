package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import java.math.BigDecimal

data class AssetWithoutTransactionsDto(
    val id: Long,
    val stockSymbol: String,
    val averageCost: BigDecimal,
    val amount: Long,
    val lifetimeBalance: BigDecimal
) : IAsset