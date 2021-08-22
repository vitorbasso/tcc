package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import org.hibernate.Hibernate
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

) : BaseEntity(), IAsset {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Asset

        return id == other.id
    }

    override fun hashCode(): Int = 469637925

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated , dateUpdated = $dateUpdated , " +
            "averageCost = $averageCost , amount = $amount , lifetimeBalance = $lifetimeBalance )"
    }
}