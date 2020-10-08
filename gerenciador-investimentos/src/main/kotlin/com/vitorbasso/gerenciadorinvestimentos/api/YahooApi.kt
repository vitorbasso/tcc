package com.vitorbasso.gerenciadorinvestimentos.api

import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.AutoCompleteDto
import com.vitorbasso.gerenciadorinvestimentos.dto.api.response.QuoteDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "yahooFeign",
    url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com"
)
interface YahooApi {

    @GetMapping("/auto-complete")
    fun searchStock(
        @RequestHeader("x-rapidapi-key") apiKey: String,
        @RequestParam q: String,
        @RequestParam region: String = "BR",
        @RequestHeader("x -rapidapi-host") host: String = "apidojo-yahoo-finance-v1.p.rapidapi.com"

    ): AutoCompleteDto

    @GetMapping("market/v2/get-quotes")
    fun getQuotes(
        @RequestHeader("x-rapidapi-key") apiKey: String,
        @RequestParam symbols: String,
        @RequestParam region: String = "BR",
        @RequestHeader("x -rapidapi-host") host: String = "apidojo-yahoo-finance-v1.p.rapidapi.com"
    ): QuoteDto

}