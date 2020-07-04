package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset

data class AssetDto(
        val stockSymbol: String,
        val averageCost: java.math.BigDecimal,
        val amount: Int
) : IAsset