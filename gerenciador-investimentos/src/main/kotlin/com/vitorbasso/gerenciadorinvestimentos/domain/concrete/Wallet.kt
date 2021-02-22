package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import java.math.BigDecimal
import java.time.LocalDate
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
    val name: String = "",
    val broker: String = "",
    override val balanceDaytrade: BigDecimal = BigDecimal.ZERO,
    override val balance: BigDecimal = BigDecimal.ZERO,
    override val withdrawn: BigDecimal = BigDecimal.ZERO,
    override val withdrawnDaytrade: BigDecimal = BigDecimal.ZERO,
    override val walletMonth: LocalDate = LocalDate.now().atStartOfMonth(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    val client: Client = Client(),

    @OneToMany(mappedBy = "wallet")
    val asset: List<Asset> = listOf()

) : BaseEntity(), IWallet, ITaxable