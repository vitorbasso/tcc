package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class Asset (
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

        @OneToMany(mappedBy = "asset")
        val transactions: List<Transaction> = listOf()

) : BaseEntity(), IAsset