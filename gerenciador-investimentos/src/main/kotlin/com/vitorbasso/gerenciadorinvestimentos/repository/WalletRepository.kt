package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<Wallet, Int> {
    fun existsByBrokerAndClientCpf(broker: String, clientCpf: String): Boolean

    fun findByBrokerAndClientCpf(broker: String, clientCpf: String): Wallet
}