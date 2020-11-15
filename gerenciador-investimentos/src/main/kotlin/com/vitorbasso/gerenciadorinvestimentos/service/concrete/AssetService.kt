package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IAssetRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
internal class AssetService(
    private val assetRepository: IAssetRepository
) {

    fun getAsset(wallet: Wallet, stock: Stock) = this.assetRepository.findByWalletAndStock(wallet, stock)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getAssetNullable(wallet: Wallet, stock: Stock) = this.assetRepository.findByWalletAndStock(wallet, stock)

    fun deleteAsset(asset: Asset) = this.assetRepository.delete(asset)

    fun saveAsset(asset: Asset) = if (asset.amount == 0)
        this.assetRepository.save(asset.copy(averageCost = BigDecimal(0), averageCount = 0))
    else this.assetRepository.save(asset)

    fun processBuyTransaction(
        asset: Asset?,
        amount: Int,
        cost: BigDecimal,
        wallet: Wallet,
        stock: Stock
    ) = asset?.copy(
        averageCost = updateAverageAssetCost(
            cost = cost,
            amount = amount,
            averageCost = asset.averageCost,
            numberOfPapersOldAverage = asset.averageCount
        ),
        amount = asset.amount + amount,
        lifetimeBalance = asset.lifetimeBalance - cost,
        averageCount = asset.averageCount + 1
    ) ?: Asset(
        averageCost = updateAverageAssetCost(cost, amount),
        amount = amount,
        lifetimeBalance = -cost,
        averageCount = 1,
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
        amount = asset.amount - amount,
        lifetimeBalance = asset.lifetimeBalance + cost
    ) ?: Asset(
        averageCost = updateAverageAssetCost(BigDecimal(0), amount),
        amount = -amount,
        lifetimeBalance = cost,
        averageCount = 0,
        wallet = wallet,
        stock = stock
    )

    private fun updateAverageAssetCost(
        cost: BigDecimal,
        amount: Int,
        averageCost: BigDecimal = BigDecimal(0),
        numberOfPapersOldAverage: Int = 0
    ) = averageCost.multiply(BigDecimal(numberOfPapersOldAverage))
        .add(cost.divide(BigDecimal(amount), 20,  RoundingMode.HALF_DOWN))
        .divide(BigDecimal(numberOfPapersOldAverage + 1), 20, RoundingMode.HALF_DOWN)

}