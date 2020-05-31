package com.basso.gerenciadorinvestimentos.application.service.concrete

import com.basso.gerenciadorinvestimentos.domain.concrete.Stock
import com.basso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.basso.gerenciadorinvestimentos.repository.AssetRepository
import org.springframework.stereotype.Service

@Service
class AssetService(
        private val assetRepository: AssetRepository
) {

    fun saveAsset(wallet: Wallet, stock: Stock) {
        TODO()
    }

}