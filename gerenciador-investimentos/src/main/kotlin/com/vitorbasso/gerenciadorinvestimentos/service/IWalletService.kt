package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet

interface IWalletService {

    fun getWallet(): IWallet

    fun getAllWallets(): List<IWallet>

}