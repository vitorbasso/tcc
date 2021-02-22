package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.TaxDeductible
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ITaxRepository : JpaRepository<TaxDeductible, Long> {
    fun findAllByClientAndMonthLessThanEqual(client: Client, month: LocalDate): List<TaxDeductible>
    fun findByClientAndMonth(client: Client, month: LocalDate): TaxDeductible?
}