package com.vitorbasso.gerenciadorinvestimentos.repository.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.Quote
import com.vitorbasso.gerenciadorinvestimentos.integration.YahooApiIntegration
import com.vitorbasso.gerenciadorinvestimentos.repository.IStockRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.StockJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class StockRepository(
    private val dbStockJpaRepository: StockJpaRepository,
    private val yahooApiIntegration: YahooApiIntegration
) : IStockRepository {

    companion object {
        const val STOCK_TTL: Long = 5 // in minutes
    }

    override fun findByTickerStartsWith(ticker: String): List<Stock> {
        TODO()
    }

    override fun findByTicker(ticker: String) = this.dbStockJpaRepository.findByIdOrNull(ticker).let {
        if (isStockInvalid(it)) this.dbStockJpaRepository.save(this.yahooApiIntegration.getQuote(ticker).getEntity())
        else it
    }

    private fun isStockInvalid(stock: Stock?)
        = stock == null || stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(LocalDateTime.now())

}

private fun Quote.getEntity() = Stock(
    ticker = this.symbol.toUpperCase(),
    currentValue = BigDecimal.valueOf(this.regularMarketPrice),
    openingValue = BigDecimal.valueOf(this.regularMarketOpen),
    closingValue = BigDecimal.valueOf(this.regularMarketPreviousClose),
    highestValue = BigDecimal.valueOf(this.regularMarketDayHigh),
    lowestValue = BigDecimal.valueOf(this.regularMarketDayLow),
    variation = BigDecimal.valueOf(this.regularMarketChangePercent)
)