package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IAssetRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
internal class AssetService(
    private val assetRepository: IAssetRepository
) {

    fun getAsset(wallet: Wallet, ticker: String) = this.assetRepository.findByWalletAndStockTicker(wallet, ticker)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getAssetNullable(wallet: Wallet, ticker: String) =
        this.assetRepository.findByWalletAndStockTicker(wallet, ticker)

    fun deleteAsset(asset: Asset) = this.assetRepository.delete(asset)

    fun saveAsset(asset: Asset) = if (asset.amount == 0)
        this.assetRepository.save(asset.copy(averageCost = BigDecimal(0)))
    else this.assetRepository.save(asset)

}