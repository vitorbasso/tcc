package com.basso.gerenciadorinvestimentos.domain.concrete

import com.basso.gerenciadorinvestimentos.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class StockAssets (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,
        val averageCost: java.math.BigDecimal = java.math.BigDecimal(0),
        val amount: Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "wallet_id", referencedColumnName = "id")
        val wallet: Wallet,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "stock_symbol", referencedColumnName = "symbol")
        val stock: Stock,

        @OneToMany(mappedBy = "stockAssets")
        val transactions: List<Transaction> = listOf()

) : BaseEntity()