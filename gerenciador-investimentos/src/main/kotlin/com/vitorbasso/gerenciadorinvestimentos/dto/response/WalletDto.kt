package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet

data class WalletDto (
        val name: String,
        val broker: String,
        val lossDaytrade: java.math.BigDecimal,
        val loss: java.math.BigDecimal,
        val balanceDaytrade: java.math.BigDecimal,
        val balance: java.math.BigDecimal,
        val stockAsset: List<AssetDto>
) : IWallet