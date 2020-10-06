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

    companion object {
        const val SYMBOL_SUFFIX = ".SA" //yahoo api necessary for symbols from b3
    }

    fun autoComplete(query: String) = this.yahooApi.searchStock(financeKey, query)

    fun getQuote(symbols: String)
        = yahooApi.getQuotes(financeKey, "$symbols$SYMBOL_SUFFIX").quoteResponse.result.firstOrNull()?.let {
        it.copy(symbol = it.symbol.substringBeforeLast(SYMBOL_SUFFIX))
    }
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
}