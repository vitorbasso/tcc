package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
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
data class Tax(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    val tax: BigDecimal = BigDecimal.ZERO,
    val daytradeTax: BigDecimal = BigDecimal.ZERO,
    val deducted: BigDecimal = BigDecimal.ZERO,
    val daytradeDeducted: BigDecimal = BigDecimal.ZERO,
    val availableToDeduct: BigDecimal = BigDecimal.ZERO,
    val daytradeAvailableToDeduct: BigDecimal = BigDecimal.ZERO,
    val month: LocalDate = LocalDate.now().withDayOfMonth(1),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    val client: Client = Client()
): BaseEntity(), ITax