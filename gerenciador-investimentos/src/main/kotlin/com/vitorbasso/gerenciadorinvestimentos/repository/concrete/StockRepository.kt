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
    private val stockJpaRepository: StockJpaRepository,
    private val yahooApi: YahooApiIntegration
) : IStockRepository {

    override fun findByTickerStartsWith(ticker: String)
        = this.stockJpaRepository.findByTickerStartsWith(ticker).takeIf { it.isNotEmpty() }
        ?: getRemoteStockList(ticker)

    override fun findByTicker(ticker: String) = this.stockJpaRepository.findByIdOrNull(ticker).let {
        if (isStockInvalid(it)) saveYahooStock(this.yahooApi.getQuote(ticker))
        else it
    }

    override fun findByTickerBatch(tickers: List<String>) : List<Stock>{
        val local = this.stockJpaRepository.findAllById(tickers).filter { !isStockInvalid(it) }
        val tickersRemotely = tickers.filter {
            !local.map { localTicker -> localTicker.ticker }.contains(it) && !it.isBlank()
        }
        return if(tickersRemotely.isEmpty()) local
        else saveYahooStockBatch(this.yahooApi.getQuoteBatch(tickersRemotely)) + local
    }

    private fun isStockInvalid(stock: Stock?)
        = stock == null || stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(LocalDateTime.now())

    private fun getRemoteStockList(ticker: String)
        = this.yahooApi.autoComplete(ticker).map {
        this.yahooApi.getQuote(it.symbol)
    }.let {
        saveYahooStockBatch(it)
    }

    private fun saveYahooStock(quote: Quote) = this.stockJpaRepository.save(quote.getEntity())

    private fun saveYahooStockBatch(quotes: List<Quote>) = quotes.map {
        it.getEntity()
    }.let {
        this.stockJpaRepository.saveAll(it)
    }

    companion object {
        const val STOCK_TTL: Long = 5 // in minutes
    }

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