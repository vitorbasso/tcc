package com.vitorbasso.gerenciadorinvestimentos.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuoteDto(
    val quoteResponse: QuoteResponse
)

data class QuoteResponse(
    val result: List<Quote>
)

data class Quote(
    val symbol: String,
    val regularMarketPrice: Double,
    val regularMarketPreviousClose: Double
)