package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class Asset(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    val averageCost: BigDecimal = BigDecimal.ZERO,
    val amount: Int = 0,
    val lifetimeBalance: BigDecimal = BigDecimal.ZERO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    val wallet: Wallet = Wallet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_ticker", referencedColumnName = "ticker")
    val stock: Stock = Stock(),

    @OneToMany(mappedBy = "asset", cascade = [CascadeType.ALL])
    val transactions: List<Transaction> = listOf()

) : BaseEntity(), IAsset