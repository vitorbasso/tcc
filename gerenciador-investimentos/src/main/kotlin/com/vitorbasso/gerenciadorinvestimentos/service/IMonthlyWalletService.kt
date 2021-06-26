package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet

interface IMonthlyWalletService {

    fun getMonthlyWallets(): List<IMonthlyWallet>

}