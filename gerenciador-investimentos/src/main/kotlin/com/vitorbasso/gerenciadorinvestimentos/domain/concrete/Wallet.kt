package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.hibernate.Hibernate
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
data class Wallet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    override val balanceDaytrade: BigDecimal = BigDecimal.ZERO,
    override val balance: BigDecimal = BigDecimal.ZERO,
    override val withdrawn: BigDecimal = BigDecimal.ZERO,
    override val withdrawnDaytrade: BigDecimal = BigDecimal.ZERO,
    override val walletMonth: LocalDate = LocalDate.now().atStartOfMonth(),

    @OneToOne(mappedBy = "wallet")
    val client: Client = Client(),

    @OneToMany(mappedBy = "wallet")
    val asset: List<Asset> = listOf()

) : BaseEntity(), IWallet, ITaxable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Wallet

        return id == other.id
    }

    override fun hashCode(): Int = 1440352806

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated , dateUpdated = $dateUpdated , " +
            "balanceDaytrade = $balanceDaytrade , balance = $balance , " +
            "withdrawn = $withdrawn , withdrawnDaytrade = $withdrawnDaytrade , walletMonth = $walletMonth )"
    }
}