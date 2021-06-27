package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class WalletServiceFacadeImpl(
    private val walletService: WalletService
) : IWalletService {

    override fun getWallet(month: LocalDate) =
        this.walletService.getWallet(SecurityContextUtil.getClientDetails(), month.atStartOfMonth())

    override fun getAllWallets() = this.walletService.getAllWallets(SecurityContextUtil.getClientDetails())

}