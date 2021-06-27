package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import java.time.LocalDate

interface IWalletService {

    fun getWallet(month: LocalDate = LocalDate.now().atStartOfMonth()): IWallet

    fun getAllWallets(): List<IWallet>

}