package com.vitorbasso.gerenciadorinvestimentos.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AutoCompleteDto(
        val quotes: List<AutoCompleteOptionDto>
)

data class AutoCompleteOptionDto(
        val exchange: String,
        val shortname: String,
        val quoteType: String,
        val symbol: String,
        val index: String,
        val score: Double,
        val typeDisp: String,
        @JsonProperty("longname")
        val longName: String,
        val isYahooFinance: Boolean
)