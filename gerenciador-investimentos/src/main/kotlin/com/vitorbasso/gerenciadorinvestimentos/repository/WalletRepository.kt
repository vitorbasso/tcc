package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<Wallet, Int> {
    fun findByBrokerAndClientId(broker: String, clientId: Long): Wallet?

    fun existsByBrokerAndClientId(broker: String, clientId: Long): Boolean
}