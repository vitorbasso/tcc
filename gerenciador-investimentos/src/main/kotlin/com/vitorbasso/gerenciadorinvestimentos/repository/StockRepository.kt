package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.Quote
import com.vitorbasso.gerenciadorinvestimentos.integration.YahooApiIntegration
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class StockRepository(
    private val dbStockRepository: IStockRepository,
    private val yahooApiIntegration: YahooApiIntegration
) {

    companion object {
        const val STOCK_TTL: Long = 5 // in minutes
    }

    fun findByTickerStartsWith(ticker: String): List<Stock> {
        TODO()
    }

    fun findByTicker(ticker: String) = this.dbStockRepository.findByIdOrNull(ticker).let {
        if (isStockInvalid(it)) this.dbStockRepository.save(this.yahooApiIntegration.getQuote(ticker).getEntity()).also { println("yahoo") }
        else it.also { println("local") }
    }

    private fun isStockInvalid(stock: Stock?)
        = stock == null || stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(LocalDateTime.now())

}

private fun Quote.getEntity() = Stock(
    ticker = this.symbol,
    currentValue = BigDecimal.valueOf(this.regularMarketPrice),
    openingValue = BigDecimal.valueOf(this.regularMarketOpen),
    closingValue = BigDecimal.valueOf(this.regularMarketPreviousClose),
    highestValue = BigDecimal.valueOf(this.regularMarketDayHigh),
    lowestValue = BigDecimal.valueOf(this.regularMarketDayLow),
    variation = BigDecimal.valueOf(this.regularMarketChangePercent)
)