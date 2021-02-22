package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ITransactionRepository : JpaRepository<Transaction, Long> {

    @Query(
        nativeQuery = true,
        value = "" +
            "select t.* " +
            "from transaction t " +
            "where t.asset_id = :assetId " +
            "and t.transaction_date >= (" +
            "select t1.transaction_date " +
            "from transaction t1 " +
            "where t1.asset_id = :assetId " +
            "and t1.transaction_date < :transactionDate " +
            "order by t1.transaction_date desc limit 1) " +
            "order by t.transaction_date"
    )
    fun findAllFromTransactionBeforeTransactionDate(assetId: Long, transactionDate: LocalDateTime): List<Transaction>

    fun findAllByAssetOrderByTransactionDate(asset: Asset): List<Transaction>

    fun existsByAssetAndTransactionDate(asset: Asset, transactionDate: LocalDateTime): Boolean

}