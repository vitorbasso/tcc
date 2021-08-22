package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
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
data class TaxDeductible(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    val deducted: BigDecimal = BigDecimal.ZERO,
    val daytradeDeducted: BigDecimal = BigDecimal.ZERO,
    val month: LocalDate = LocalDate.now().atStartOfMonth(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    val client: Client = Client()
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaxDeductible

        return id == other.id
    }

    override fun hashCode(): Int = 1181996472

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , dateCreated = $dateCreated , dateUpdated = $dateUpdated , " +
            "deducted = $deducted , daytradeDeducted = $daytradeDeducted , month = $month )"
    }
}