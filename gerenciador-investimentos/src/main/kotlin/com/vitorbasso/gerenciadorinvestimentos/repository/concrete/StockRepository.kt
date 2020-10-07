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

    override fun findByTickerStartsWith(ticker: String)
        = this.dbStockJpaRepository.findByTickerStartsWith(ticker).takeIf { it.isNotEmpty() }
        ?: getRemoteStockList(ticker)

    override fun findByTicker(ticker: String) = this.dbStockJpaRepository.findByIdOrNull(ticker).let {
        if (isStockInvalid(it)) saveYahooStock(this.yahooApiIntegration.getQuote(ticker))
        else it
    }

    private fun isStockInvalid(stock: Stock?)
        = stock == null || stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(LocalDateTime.now())

    private fun getRemoteStockList(ticker: String) = this.yahooApiIntegration.autoComplete(ticker).quotes.filter { listItem ->
        listItem.symbol.endsWith(YahooApiIntegration.SYMBOL_SUFFIX)
    }.map {filteredListItem ->
        this.yahooApiIntegration.getQuote(filteredListItem.symbol).getEntity()
    }.let {remoteList ->
        this.dbStockJpaRepository.saveAll(remoteList)
    }

    private fun saveYahooStock(quote: Quote) = this.dbStockJpaRepository.save(quote.getEntity())

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