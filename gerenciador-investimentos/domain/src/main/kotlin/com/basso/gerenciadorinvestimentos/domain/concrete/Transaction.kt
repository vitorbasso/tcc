package com.basso.gerenciadorinvestimentos.domain.concrete

import com.basso.gerenciadorinvestimentos.domain.BaseEntity
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
        val id: Long,

        val type: String,

        val quantity: Int,

        val value: java.math.BigDecimal,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "stock_symbol", referencedColumnName = "symbol")
        val stock: Stock,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "asset_id", referencedColumnName = "id")
        val asset: Asset

) : BaseEntity()