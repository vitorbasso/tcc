package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
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

    fun deleteAsset(asset: Asset) = this.assetRepository.delete(asset)

    fun addTransactionToAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal,
        type: TransactionType
    ) = (this.assetRepository.findByWalletAndStock(wallet, stock).let {
        when (type) {
            TransactionType.BUY -> it.processBuyTransaction(
                amount = amount,
                cost = cost,
                wallet = wallet,
                stock = stock
            )
            TransactionType.SELL -> it.processSellTransaction(
                amount = amount,
                cost = cost,
                wallet = wallet,
                stock = stock
            )
        }
    }).let { saveAsset(it) }

    private fun saveAsset(asset: Asset) = if (asset.amount == 0)
        this.assetRepository.save(asset.copy(averageCost = BigDecimal(0), averageCount = 0))
    else this.assetRepository.save(asset)

    private fun Asset?.processBuyTransaction(
        amount: Int,
        cost: BigDecimal,
        wallet: Wallet,
        stock: Stock
    ) = this?.copy(
        averageCost = updateAverageAssetCost(
            cost = cost,
            amount = amount,
            averageCost = this.averageCost,
            numberOfTransactions = this.averageCount
        ),
        amount = this.amount + amount,
        lifetimeBalance = this.lifetimeBalance - cost,
        averageCount = this.averageCount + 1
    ) ?: Asset(
        averageCost = updateAverageAssetCost(cost, amount),
        amount = amount,
        lifetimeBalance = -cost,
        averageCount = 1,
        wallet = wallet,
        stock = stock
    )

    private fun Asset?.processSellTransaction(
        amount: Int,
        cost: BigDecimal,
        wallet: Wallet,
        stock: Stock
    ) = this?.copy(
        amount = this.amount - amount,
        lifetimeBalance = this.lifetimeBalance + cost
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
        numberOfTransactions: Int = 0
    ) = (averageCost * BigDecimal(numberOfTransactions) + (cost / BigDecimal(amount))) /
        (BigDecimal(numberOfTransactions) + BigDecimal(1))

}