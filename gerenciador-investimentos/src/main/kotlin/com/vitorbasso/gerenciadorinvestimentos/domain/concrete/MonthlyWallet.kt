package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
data class MonthlyWallet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val broker: String,
    val monthlyBalanceDaytrade: BigDecimal = BigDecimal(0),
    val monthlyBalance: BigDecimal = BigDecimal(0),
    val walletId: Long,
    val walletMonth: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    val client: Client

) : BaseEntity()