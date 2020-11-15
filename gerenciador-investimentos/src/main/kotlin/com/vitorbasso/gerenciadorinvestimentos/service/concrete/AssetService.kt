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
        averageCost = updateAverageAssetCost(
            cost = cost,
            amount = amount,
            averageValueCount = asset.averageValueCount,
            averageQuantityCount = asset.averageQuantityCount
        ),
        amount = asset.amount + amount,
        lifetimeBalance = asset.lifetimeBalance - cost,
        averageQuantityCount = asset.averageQuantityCount + amount,
        averageValueCount = asset.averageValueCount.add(cost)
    ) ?: Asset(
        averageCost = updateAverageAssetCost(cost, amount),
        amount = amount,
        lifetimeBalance = -cost,
        averageQuantityCount = amount,
        averageValueCount = cost,
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
        averageCost = updateAverageAssetCost(BigDecimal.ZERO, amount),
        amount = -amount,
        lifetimeBalance = cost,
        wallet = wallet,
        stock = stock
    )

    private fun updateAverageAssetCost(
        cost: BigDecimal,
        amount: Int,
        averageValueCount: BigDecimal = BigDecimal(0),
        averageQuantityCount: Int = 0
    ) = averageValueCount.add(cost)
        .divide(BigDecimal(averageQuantityCount + amount), 20, RoundingMode.HALF_EVEN)

}