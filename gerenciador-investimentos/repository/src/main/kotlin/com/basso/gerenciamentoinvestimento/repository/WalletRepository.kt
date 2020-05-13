package com.basso.gerenciamentoinvestimento.repository

import com.basso.gerenciadorinvestimentos.application.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository : JpaRepository<Wallet, Int>