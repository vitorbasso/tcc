package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockJpaRepository : JpaRepository<Stock, String>{
    fun findByTickerStartsWith(ticker: String): List<Stock>
}