package com.basso.gerenciadorinvestimentos.application.dto.response

import com.basso.gerenciadorinvestimentos.domain.IAsset

data class AssetDto(
        val stockSymbol: String,
        val averageCost: java.math.BigDecimal,
        val amount: Int
) : IAsset