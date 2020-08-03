package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest

interface IWalletService {

    fun getWalletCollection() : List<IWallet>

    fun getWallet(broker: String) : IWallet

    fun saveWallet(walletToSave: IWallet) : IWallet

    fun updateWallet(broker: String, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(broker: String)

}