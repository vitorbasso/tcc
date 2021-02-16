package com.vitorbasso.gerenciadorinvestimentos.dto.api.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AutoCompleteDto(
    val quotes: List<AutoCompleteOptionDto>
)

data class AutoCompleteOptionDto(
    val symbol: String
)