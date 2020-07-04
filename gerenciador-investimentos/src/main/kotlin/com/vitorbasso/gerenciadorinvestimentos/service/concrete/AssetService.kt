package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.repository.AssetRepository
import org.springframework.stereotype.Service

@Service
class AssetService(
        private val assetRepository: AssetRepository
) {

    fun saveAsset(wallet: Wallet, stock: Stock) {
        TODO()
    }

}