package com.vitorbasso.gerenciadorinvestimentos.repository.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.Quote
import com.vitorbasso.gerenciadorinvestimentos.integration.Spark
import com.vitorbasso.gerenciadorinvestimentos.integration.YahooApiIntegration
import com.vitorbasso.gerenciadorinvestimentos.repository.IStockRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.StockJpaRepository
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDateTime

@Repository
class StockRepository(
    private val stockJpaRepository: StockJpaRepository,
    private val yahooApi: YahooApiIntegration
) : IStockRepository {

    override fun findByTickerStartsWith(ticker: String) =
        this.stockJpaRepository.findByTickerStartsWith(ticker)
            .takeIf { it.isNotEmpty() }
            ?: getRemoteStockList(ticker)

    override fun findByTicker(ticker: String) = findByTickerBatch(listOf(ticker)).firstOrNull()

    override fun findByTickerBatch(tickers: List<String>): List<Stock> {
        val nonBlankTickers = tickers.filter { it.isNotBlank() }.map { it.replace(whitespaceRegex, "") }
        val localStocks = this.stockJpaRepository.findAllById(nonBlankTickers)
        val localValidStocks = localStocks.filter { !isStockInvalid(it) }

        val localValidStocksTickers = localValidStocks.map { it.ticker }.toSet()
        val quotesTickers = nonBlankTickers.filter {
            !localValidStocksTickers.contains(it)
        }
        val sparksTickers = quotesTickers.filter { ticker ->
            val stock = localStocks.find { it.ticker == ticker }
            stock == null || notUpdatedToday(stock)
        }
        return if (quotesTickers.isEmpty()) localValidStocks
        else {
            runBlocking(Dispatchers.IO) {
                val quotes = async { yahooApi.getQuoteBatch(quotesTickers) }
                val sparks = async {
                    if (sparksTickers.isNotEmpty())
                        yahooApi.getHistoricalData(sparksTickers)
                    else listOf()
                }
                saveYahooStockBatch(
                    locals = localStocks,
                    quotes = quotes.await(),
                    sparks = sparks.await()
                ) + localValidStocks
            }
        }
    }

    private fun getRemoteStockList(ticker: String) = this.yahooApi.autoComplete(ticker).map {
        it.symbol
    }.let {
        findByTickerBatch(it)
    }

    private fun saveYahooStockBatch(
        locals: List<Stock>,
        quotes: List<Quote>,
        sparks: List<Spark>
    ) = quotes.map {
        getStock(
            quote = it,
            closes = sparks.find { spark -> spark.symbol == it.symbol },
            local = locals.find { local -> local.ticker == it.symbol })
    }.let {
        this.stockJpaRepository.saveAll(it)
    }

    private fun getStock(quote: Quote, closes: Spark?, local: Stock?): Stock {
        val curValue = BigDecimal.valueOf(quote.regularMarketPrice)
        return Stock(
            ticker = quote.symbol.toUpperCase(),
            currentValue = curValue,
            lastClose = BigDecimal.valueOf(quote.regularMarketPreviousClose),
            lastWeekClose = closes?.lastWeekClose ?: local?.lastWeekClose ?: curValue,
            lastMonthClose = closes?.lastMonthClose ?: local?.lastMonthClose ?: curValue,
            lastYearClose = closes?.lastYearClose ?: local?.lastYearClose ?: curValue
        )
    }

    private fun isStockInvalid(stock: Stock?): Boolean {
        val now = LocalDateTime.now()
        val daysPastSaturday = now.dayOfWeek.value.toLong() % 8 - 6
        val lastSaturday = now.minusDays(daysPastSaturday).atStartOfDay()
        return stock == null
            || (
            (((now.dayOfWeek !in weekend) && (!now.minusDays(1).atStartOfDay()
                .isEqual(stock.dateUpdated.atStartOfDay()) || (now.hour !in 9..18 && stock.dateUpdated.hour in 9..18)))
                || lastSaturday.isAfter(
                stock.dateUpdated
            ))
                && stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(now)
            )
    }


    private fun notUpdatedToday(stock: Stock) =
        stock.dateUpdated.atStartOfDay().isBefore(
            LocalDateTime.now().atStartOfDay()
        ) || stock.lastWeekClose == null || stock.lastMonthClose == null || stock.lastYearClose == null

    companion object {
        const val STOCK_TTL: Long = 5 // in minutes
        private val whitespaceRegex = "\\s".toRegex()
        private val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }
}



