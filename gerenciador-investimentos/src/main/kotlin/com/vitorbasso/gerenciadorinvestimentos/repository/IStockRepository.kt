package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import org.springframework.stereotype.Repository

@Repository
interface IStockRepository {
    fun findByTickerStartsWith(ticker: String): List<Stock>

    fun findByTicker(ticker: String): Stock?

    fun findByTickerBatch(tickers: List<String>) : List<Stock>
}