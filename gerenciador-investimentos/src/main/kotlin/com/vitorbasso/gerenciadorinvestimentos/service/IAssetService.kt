package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import java.math.BigDecimal

interface IAssetService {
    fun deleteAsset(broker: String, ticker: String)

    fun addTransactionToAsset(
        wallet: Wallet,
        stock: Stock,
        amount: Int,
        cost: BigDecimal,
        type: TransactionType
    ) : Asset
}