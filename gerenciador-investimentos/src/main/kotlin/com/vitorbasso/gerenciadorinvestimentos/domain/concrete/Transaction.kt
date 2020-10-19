package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
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

        val type: TransactionType,

        val quantity: Int,

        val value: java.math.BigDecimal,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "asset_id", referencedColumnName = "id")
        val asset: Asset

) : BaseEntity()