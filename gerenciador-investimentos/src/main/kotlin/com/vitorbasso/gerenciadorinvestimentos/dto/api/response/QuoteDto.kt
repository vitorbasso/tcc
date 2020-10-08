package com.vitorbasso.gerenciadorinvestimentos.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuoteDto (
    val quoteResponse: QuoteResponse
)

data class QuoteResponse(
    val result: List<Quote>
)

data class Quote(
    val symbol: String,
    val regularMarketOpen: Double,
    val regularMarketChangePercent: Double,
    val regularMarketPrice: Double,
    val regularMarketDayLow: Double,
    val regularMarketDayHigh: Double,
    val regularMarketPreviousClose: Double
)