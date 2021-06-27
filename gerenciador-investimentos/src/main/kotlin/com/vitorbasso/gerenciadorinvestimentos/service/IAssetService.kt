package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet

interface IAssetService {

    fun getAsset(wallet: Wallet, ticker: String): Asset

    fun deleteAsset(walletId: Long, ticker: String)
}