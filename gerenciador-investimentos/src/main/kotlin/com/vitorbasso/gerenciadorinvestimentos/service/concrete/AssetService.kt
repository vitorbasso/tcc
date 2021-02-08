package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
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

    fun getAsset(wallet: Wallet, stock: Stock) = this.assetRepository.findByWalletAndStock(wallet, stock)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getAssetNullable(wallet: Wallet, stock: Stock) = this.assetRepository.findByWalletAndStock(wallet, stock)

    fun deleteAsset(asset: Asset) = this.assetRepository.delete(asset)

    fun saveAsset(asset: Asset) = if (asset.amount == 0)
        this.assetRepository.save(asset.copy(
            averageCost = BigDecimal(0),
            averageQuantityCount = 0,
            averageValueCount = BigDecimal.ZERO
        ))
    else this.assetRepository.save(asset)

    fun processBuyTransaction(
        asset: Asset?,
        amount: Int,
        cost: BigDecimal,
        wallet: Wallet,
        stock: Stock
    ) = asset?.copy(
        amount = asset.amount + amount
    ) ?: Asset(
        amount = amount,
        wallet = wallet,
        stock = stock
    )

    fun processSellTransaction(
        asset: Asset?,
        amount: Int,
        cost: BigDecimal,
        wallet: Wallet,
        stock: Stock
    ) = asset?.copy(
        amount = asset.amount - amount
    ) ?: Asset(
        amount = -amount
    )

}