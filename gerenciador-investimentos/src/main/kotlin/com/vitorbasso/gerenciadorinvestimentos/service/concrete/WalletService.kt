package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class WalletService(
    private val walletRepository: IWalletRepository
) {

    fun getWallet(
        client: Client,
        month: LocalDate
    ) = this.walletRepository.findByClientAndWalletMonth(client, checkDate(month)) ?: Wallet(
        walletMonth = month,
        client = client
    ).let { this.walletRepository.save(it) }

    fun getAllWallets(client: Client) = this.walletRepository.findAllByClient(client)

    fun checkDate(month: LocalDate) =
        month.takeUnless { it.atStartOfMonth().isAfter(LocalDate.now()) } ?: throw CustomWrongDateException()

}