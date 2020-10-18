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

    fun updateAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal
    ) = this.assetRepository.findByWalletAndStock(wallet, stock)?.let {
        it.copy(
            averageCost = newAverage(
                averageCost = it.averageCost,
                numberOfTransactions = it.numberOfTransactions,
                cost = cost
            ),
            amount = it.amount + amount,
            numberOfTransactions = it.numberOfTransactions + 1
        )
    } ?: Asset(
        averageCost = cost,
        amount = amount,
        numberOfTransactions = 1,
        wallet = wallet,
        stock = stock
    )

    private fun newAverage(
        averageCost: BigDecimal,
        numberOfTransactions: Int,
        cost: BigDecimal
    ) = (averageCost * BigDecimal(numberOfTransactions) + cost) /
        (BigDecimal(numberOfTransactions) + BigDecimal(1))


}