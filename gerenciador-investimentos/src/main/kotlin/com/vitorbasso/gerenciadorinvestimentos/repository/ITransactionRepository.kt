package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ITransactionRepository : JpaRepository<Transaction, Long> {

    fun findByAssetAndTransactionDateOrderByTransactionDate(asset: Asset, transactionDate: LocalDateTime): List<Transaction>

    @Query("select t " +
        "from Transaction t " +
        "where month(t.transactionDate)=:transactionMonth " +
        "and t.asset=:asset")
    fun findByMonth(asset: Asset, transactionMonth: Int): List<Transaction>

}