package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface IWalletRepository : JpaRepository<Wallet, Long> {
    fun findByClientAndWalletMonth(client: Client, walletMonth: LocalDate): Wallet?
    fun findAllByClient(client: Client): List<Wallet>
}