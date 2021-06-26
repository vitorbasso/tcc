package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IWalletRepository : JpaRepository<Wallet, Long> {
    fun findByClientId(clientId: Long): Wallet?
    fun existsByClient(client: Client): Boolean
}