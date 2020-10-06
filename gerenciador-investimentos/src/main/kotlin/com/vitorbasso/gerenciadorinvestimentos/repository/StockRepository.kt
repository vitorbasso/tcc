package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.QuoteDto
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
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
            if(isStockInvalid(it)) this.dbStockRepository.save(this.yahooApiIntegration.getQuote(ticker).getEntity())
            else it
        }

    private fun isStockInvalid(stock: Stock?)
        = stock == null || stock.dateUpdated.plusMinutes(STOCK_TTL).isBefore(LocalDateTime.now())

}

private fun QuoteDto.getEntity() = this.quoteResponse.result.firstOrNull()?.let {
    Stock(
        ticker = it.symbol.substring(0, it.symbol.length - 3),
        currentValue = BigDecimal.valueOf(it.regularMarketPrice),
        openingValue = BigDecimal.valueOf(it.regularMarketOpen),
        closingValue = BigDecimal.valueOf(it.regularMarketPreviousClose),
        highestValue = BigDecimal.valueOf(it.regularMarketDayHigh),
        lowestValue = BigDecimal.valueOf(it.regularMarketDayLow),
        variation = BigDecimal.valueOf(it.regularMarketChangePercent)
    )
} ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)