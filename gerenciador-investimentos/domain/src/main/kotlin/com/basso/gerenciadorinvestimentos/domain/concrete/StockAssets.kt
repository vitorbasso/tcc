package com.basso.gerenciadorinvestimentos.domain.concrete

import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class StockAssets (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,
        val averageCost: java.math.BigDecimal,
        val amount: Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wallet_id", referencedColumnName = "id")
        val wallet: Wallet,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "stock_symbol", referencedColumnName = "symbol")
        val stock: Stock,

        @Transient
        val dateUpdate: Timestamp? = null
) : BaseEntity()