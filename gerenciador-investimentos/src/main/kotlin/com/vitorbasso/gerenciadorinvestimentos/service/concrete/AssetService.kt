package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.repository.IAssetRepository
import org.springframework.stereotype.Service

@Service
class AssetService(
        private val assetRepository: IAssetRepository
) {

    fun saveAsset(wallet: Wallet, stock: Stock) {
        TODO()
    }

}