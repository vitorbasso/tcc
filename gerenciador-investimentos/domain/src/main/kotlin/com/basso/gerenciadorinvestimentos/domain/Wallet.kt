package com.basso.gerenciadorinvestimentos.domain

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne

@Entity
data class Wallet(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,
        val name: String,
        val broker: String,
        val lossDaytrade: java.math.BigDecimal,
        val loss: java.math.BigDecimal,
        val balanceDaytrade: java.math.BigDecimal,
        val balance: java.math.BigDecimal,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "client_email", referencedColumnName = "email")
        val client: Client,

        @OneToOne(mappedBy = "wallet")
        val stockAssets: StockAssets
)