package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest

interface IWalletService {

    fun getWalletCollection() : List<IWallet>

    fun getWallet(walletId: Long) : IWallet

    fun saveWallet(walletToSave: IWallet) : IWallet

    fun updateWallet(walletId: Long, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(walletId: Long)

}