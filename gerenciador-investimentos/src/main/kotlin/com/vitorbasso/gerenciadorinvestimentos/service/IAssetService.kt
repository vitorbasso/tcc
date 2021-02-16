package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet

interface IAssetService {

    fun getAsset(wallet: Wallet, stock: Stock): Asset

    fun deleteAsset(walletId: Long, ticker: String)
}