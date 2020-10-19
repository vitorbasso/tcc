package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
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
        assetBalance = -cost,
        numberOfTransactions = 1,
        wallet = wallet,
        stock = stock
    )).let { this.assetRepository.save(it) }

    fun subtractFromAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal
    ) = (this.assetRepository.findByWalletAndStock(wallet, stock)?.takeUnless {
        it.amount - amount < 0
    } ?: throw CustomBadRequestException(ManagerErrorCode.MANAGER_10)).let {
        this.assetRepository.save(performSubtraction(
            asset = it,
            amount = amount,
            cost = cost
        ))
    }

    private fun performAddition(asset: Asset, amount: Int, cost: BigDecimal) = asset.copy(
        averageCost = getAverageAssetCost(
            cost = cost,
            amount = amount,
            averageCost = asset.averageCost,
            numberOfTransactions = asset.numberOfTransactions
        ),
        amount = asset.amount + amount,
        assetBalance = asset.assetBalance - cost,
        numberOfTransactions = asset.numberOfTransactions + 1
    )

    private fun performSubtraction(asset: Asset, amount: Int, cost: BigDecimal)
        = asset.copy(amount = asset.amount - amount, assetBalance = asset.assetBalance + cost).let {
        if (it.amount == 0) it.copy(averageCost = BigDecimal(0), numberOfTransactions = 0)
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