package com.basso.gerenciadorinvestimentos.domain.concrete

import com.basso.gerenciadorinvestimentos.domain.BaseEntity
import com.basso.gerenciadorinvestimentos.domain.IWallet
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
        val id: Long = 0,
        val name: String,
        val broker: String,
        val lossDaytrade: java.math.BigDecimal = java.math.BigDecimal(0),
        val loss: java.math.BigDecimal = java.math.BigDecimal(0),
        val balanceDaytrade: java.math.BigDecimal = java.math.BigDecimal(0),
        val balance: java.math.BigDecimal = java.math.BigDecimal(0),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "client_cpf", referencedColumnName = "cpf")
        val client: Client,

        @OneToMany(mappedBy = "wallet")
        val asset: List<Asset> = listOf()

) : BaseEntity(), IWallet