package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.dto.response.AssetDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.TransactionDto
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.util.setScale
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class AssetServiceProxyImpl(
    @Qualifier("assetServiceFacadeImpl")
    private val assetService: IAssetService
) : IAssetService {
    override fun getAsset(ticker: String): IAsset {
        return this.assetService.getAsset(ticker).toDto()
    }

    override fun deleteAsset(ticker: String) {
        this.assetService.deleteAsset(ticker)
    }
}

private fun IAsset.toDto() = AssetDto(
    id = (this as Asset).id,
    stockSymbol = this.stock.ticker,
    averageCost = this.averageCost,
    amount = this.amount,
    lifetimeBalance = this.lifetimeBalance,
    transactions = this.transactions.map { it.getDto() }
)

private fun ITransaction.getDto() = TransactionDto(
    id = (this as Transaction).id,
    type = this.type,
    quantity = this.quantity,
    value = this.value.setScale(),
    transactionDate = this.transactionDate,
)