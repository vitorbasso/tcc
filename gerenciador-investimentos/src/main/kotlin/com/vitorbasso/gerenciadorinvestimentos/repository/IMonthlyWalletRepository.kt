package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface IMonthlyWalletRepository : JpaRepository<MonthlyWallet, Long> {

    fun findAllByClientId(clientId: Long): List<MonthlyWallet>

    fun findByWalletMonth(walletMonth: LocalDate): MonthlyWallet?

    fun existsByWalletIdAndWalletMonth(walletId: Long, walletMonth: LocalDate): Boolean
}