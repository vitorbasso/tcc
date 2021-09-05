package com.vitorbasso.gerenciadorinvestimentos.integration

import com.vitorbasso.gerenciadorinvestimentos.api.YahooApi
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class YahooApiIntegration(
    private val yahooApi: YahooApi,
    @Value("\${finance.api.key}")
    private val financeKey: String
) {

    fun autoComplete(query: String) = this.yahooApi.searchStock(financeKey, query).quotes.filter { listItem ->
        listItem.symbol.endsWith(YahooApiIntegration.SYMBOL_SUFFIX)
    }

    fun getQuote(symbol: String) = yahooApi.getQuotes(financeKey, getProcessedSymbol(symbol))
        .quoteResponse.result.firstOrNull()?.let {
            it.copy(symbol = cleanSymbol(it.symbol))
        } ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getQuoteBatch(symbols: List<String>) = this.yahooApi.getQuotes(
        financeKey,
        symbols.joinToString(separator = ",", transform = ::getProcessedSymbol)
    ).quoteResponse.result.map {
        it.copy(symbol = cleanSymbol(it.symbol))
    }

    fun getHistoricalData(symbols: List<String>) = this.yahooApi.getHistoricalData(
        financeKey,
        symbols.joinToString(separator = ",", transform = ::getProcessedSymbol).replace(whitespaceRegex, "")
    ).map { (key, value) ->
        Spark.from(cleanSymbol(key), value)
    }

    private fun getProcessedSymbol(rawSymbol: String) =
        if (!rawSymbol.startsWith("^")) "${cleanSymbol(rawSymbol)}$SYMBOL_SUFFIX" else rawSymbol.toUpperCase()

    private fun cleanSymbol(processedSymbol: String) = processedSymbol.substringBeforeLast(SYMBOL_SUFFIX).toUpperCase()

    companion object {
        const val SYMBOL_SUFFIX = ".SA" //yahoo api necessary for symbols from b3
        private val whitespaceRegex = "\\s".toRegex()
    }

}