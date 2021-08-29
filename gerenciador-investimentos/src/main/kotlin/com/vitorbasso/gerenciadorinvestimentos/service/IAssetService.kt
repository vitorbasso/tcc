package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset

interface IAssetService {

    fun getAsset(ticker: String): IAsset

    fun deleteAsset(ticker: String)
}