package com.basso.gerenciadorinvestimentos.application

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
        val id: Int,
        val name: String,
        val broker: String,
        val lossDaytrade: Long,
        val loss: Long,
        val balanceDaytrade: Long,
        val balance: Long,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "client_email", referencedColumnName = "email")
        val client: Client,

        @OneToOne(mappedBy = "wallet")
        val stockAssets: StockAssets
)