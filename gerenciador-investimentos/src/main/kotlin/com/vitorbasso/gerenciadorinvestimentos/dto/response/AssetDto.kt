package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import java.math.BigDecimal

data class AssetDto(
    val id: Long,
    val stockSymbol: String,
    val averageCost: BigDecimal,
    val amount: Int,
    val lifetimeBalance: BigDecimal
) : IAsset