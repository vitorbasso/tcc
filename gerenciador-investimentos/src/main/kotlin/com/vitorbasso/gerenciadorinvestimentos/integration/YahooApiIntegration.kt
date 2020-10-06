package com.vitorbasso.gerenciadorinvestimentos.integration

import com.vitorbasso.gerenciadorinvestimentos.api.YahooApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class YahooApiIntegration (
    private val yahooApi: YahooApi,
    @Value("\${finance.api.key}")
    private val financeKey: String
){
    fun autoComplete(query: String) = this.yahooApi.searchStock(financeKey, query)

    fun getQuote(symbols: String) = yahooApi.getQuotes(financeKey, "$symbols.SA")
}