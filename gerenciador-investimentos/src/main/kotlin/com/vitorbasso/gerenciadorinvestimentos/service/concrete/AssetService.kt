package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.repository.IAssetRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AssetService(
    private val assetRepository: IAssetRepository
) {

    fun addToAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal
    ) = (this.assetRepository.findByWalletAndStock(wallet, stock)?.let {
        performAddition(
            asset = it,
            amount = amount,
            cost = cost
        )
    } ?: Asset(
        averageCost = getAverageAssetCost(cost, amount),
        amount = amount,
        lifetimeBalance = -cost,
        averageCount = 1,
        wallet = wallet,
        stock = stock
    )).let { saveAsset(it) }

    fun subtractFromAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal
    ) = (this.assetRepository.findByWalletAndStock(wallet, stock)?.let {
        performSubtraction(
            asset = it,
            amount = amount,
            cost = cost
        )
    } ?: Asset(
        averageCost = getAverageAssetCost(BigDecimal(0), amount),
        amount = -amount,
        lifetimeBalance = cost,
        averageCount = 0,
        wallet = wallet,
        stock = stock
    )).let { saveAsset(it) }

    private fun saveAsset(asset: Asset)
        = if(asset.amount == 0) this.assetRepository.save(asset.copy(averageCost = BigDecimal(0), averageCount = 0))
    else this.assetRepository.save(asset)

    private fun performAddition(asset: Asset, amount: Int, cost: BigDecimal) = asset.copy(
        averageCost = getAverageAssetCost(
            cost = cost,
            amount = amount,
            averageCost = asset.averageCost,
            numberOfTransactions = asset.averageCount
        ),
        amount = asset.amount + amount,
        lifetimeBalance = asset.lifetimeBalance - cost,
        averageCount = asset.averageCount + 1
    )

    private fun performSubtraction(asset: Asset, amount: Int, cost: BigDecimal)
        = asset.copy(amount = asset.amount - amount, lifetimeBalance = asset.lifetimeBalance + cost).let {
        if (it.amount == 0) it.copy(averageCost = BigDecimal(0), averageCount = 0)
        else it
    }

    private fun getAverageAssetCost(
        cost: BigDecimal,
        amount: Int,
        averageCost: BigDecimal = BigDecimal(0),
        numberOfTransactions: Int = 0
    ) = (averageCost * BigDecimal(numberOfTransactions) + (cost / BigDecimal(amount))) /
        (BigDecimal(numberOfTransactions) + BigDecimal(1))

}