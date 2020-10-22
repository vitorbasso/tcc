package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ITransactionRepository : JpaRepository<Transaction, Long> {

    fun findByAssetAndTransactionDate(asset: Asset, transactionDate: LocalDate): List<Transaction>

}