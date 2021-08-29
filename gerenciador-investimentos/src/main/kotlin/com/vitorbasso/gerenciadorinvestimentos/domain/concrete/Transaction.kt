package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.LocalDateTime
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
    val id: Long = -1,

    val type: TransactionType = TransactionType.BUY,

    val quantity: Long = 0,

    val value: BigDecimal = BigDecimal.ZERO,

    val transactionDate: LocalDateTime = LocalDateTime.now(),

    val checkingValue: BigDecimal = BigDecimal.ZERO,

    val checkingQuantity: Long = 0,

    val daytradeQuantity: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    val asset: Asset = Asset()

) : BaseEntity(), ITransaction {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Transaction

        return id == other.id
    }

    override fun hashCode(): Int = 304080742

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated , dateUpdated = $dateUpdated , " +
            "type = $type , quantity = $quantity , value = $value , transactionDate = $transactionDate , " +
            "checkingValue = $checkingValue , checkingQuantity = $checkingQuantity , " +
            "daytradeQuantity = $daytradeQuantity )"
    }
}