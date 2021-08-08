package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IMonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.hibernate.Hibernate
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
    val name: String = "",
    val broker: String = "",
    override val balanceDaytrade: BigDecimal = BigDecimal.ZERO,
    override val balance: BigDecimal = BigDecimal.ZERO,
    override val withdrawn: BigDecimal = BigDecimal.ZERO,
    override val withdrawnDaytrade: BigDecimal = BigDecimal.ZERO,
    val walletId: Long = 0,
    override val walletMonth: LocalDate = LocalDate.now().atStartOfMonth(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    val client: Client = Client()

) : BaseEntity(), IMonthlyWallet, ITaxable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MonthlyWallet

        return id == other.id
    }

    override fun hashCode(): Int = 163527892

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated , dateUpdated = $dateUpdated , " +
            "name = $name , broker = $broker , balanceDaytrade = $balanceDaytrade , balance = $balance , " +
            "withdrawn = $withdrawn , withdrawnDaytrade = $withdrawnDaytrade , walletId = $walletId , " +
            "walletMonth = $walletMonth )"
    }
}