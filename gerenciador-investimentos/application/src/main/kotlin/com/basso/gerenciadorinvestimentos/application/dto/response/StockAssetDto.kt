package com.basso.gerenciadorinvestimentos.application.dto.response

import com.basso.gerenciadorinvestimentos.domain.IStockAsset

data class StockAssetDto(
        val stockSymbol: String,
        val averageCost: java.math.BigDecimal,
        val amount: Int
) : IStockAsset