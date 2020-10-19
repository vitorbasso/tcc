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
        it.copy(
            averageCost = getAverageAssetCost(
                cost = cost,
                amount = amount,
                averageCost = it.averageCost,
                numberOfTransactions = it.numberOfTransactions
            ),
            amount = it.amount + amount,
            assetBalance = it.assetBalance - cost,
            numberOfTransactions = it.numberOfTransactions + 1
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
        it.copy(amount = it.amount - amount, assetBalance = it.assetBalance + cost).let { updatedAsset ->
            if(updatedAsset.amount != 0)
                this.assetRepository.save(updatedAsset)
            else
                this.assetRepository.save(updatedAsset.copy(averageCost = BigDecimal(0), numberOfTransactions = 0))
        }
    }

    private fun getAverageAssetCost(
        cost: BigDecimal,
        amount: Int,
        averageCost: BigDecimal = BigDecimal(0),
        numberOfTransactions: Int = 0
    ) = (averageCost * BigDecimal(numberOfTransactions) + (cost / BigDecimal(amount))) /
        (BigDecimal(numberOfTransactions) + BigDecimal(1))

}