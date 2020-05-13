package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<Wallet, Int> {

}