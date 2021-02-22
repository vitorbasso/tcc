package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet

interface IMonthlyWalletService {
    fun getMonthlyWallets(): List<IMonthlyWallet>

    fun getMonthlyWallet(monthlyWalletId: Long): IMonthlyWallet

    fun deleteMonthlyWallet(monthlyWalletId: Long)
}