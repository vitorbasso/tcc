package com.vitorbasso.gerenciadorinvestimentos.service

interface IAssetService {
    fun deleteAsset(broker: String, ticker: String)
}