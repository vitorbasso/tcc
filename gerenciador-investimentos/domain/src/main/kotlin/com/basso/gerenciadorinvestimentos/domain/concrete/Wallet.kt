package com.basso.gerenciadorinvestimentos.domain.concrete

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

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
        @JoinColumn(name = "client_cpf", referencedColumnName = "cpf")
        val client: Client,

        @OneToMany(mappedBy = "wallet")
        val stockAssets: List<StockAssets> = listOf()

) : BaseEntity()