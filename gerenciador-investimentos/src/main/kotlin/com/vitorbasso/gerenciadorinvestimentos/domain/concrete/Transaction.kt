package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class Transaction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val type: TransactionType = TransactionType.BUY,

    val quantity: Int = 0,

    val value: BigDecimal = BigDecimal.ZERO,

    val transactionDate: LocalDateTime = LocalDateTime.now(),

    val checkingValue: BigDecimal = BigDecimal.ZERO,

    val checkingQuantity: Int = 0,

    val daytrade: Boolean = false,

    val daytradeQuantity: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    val asset: Asset = Asset()

) : BaseEntity(), ITransaction