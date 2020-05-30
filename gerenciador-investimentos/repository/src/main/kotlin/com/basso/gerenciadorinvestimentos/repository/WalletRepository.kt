package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<Wallet, Int> {
    fun existsByBrokerAndClient_Cpf(broker: String, clientCpf: String): Boolean

    fun findByBrokerAndClient_Cpf(broker: String, clientCpf: String): Wallet
}