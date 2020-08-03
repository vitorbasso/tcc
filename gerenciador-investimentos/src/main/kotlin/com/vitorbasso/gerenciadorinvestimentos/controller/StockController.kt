package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/stocks")
class StockController(
        private val stockService: IStockService
) {

    @GetMapping("/{ticker}")
    fun getStock(@PathVariable ticker: String) = this.stockService.getStock(ticker.toUpperCase())

    @GetMapping("/tickers/{ticker}")
    fun getStockStartingWith(@PathVariable ticker: String)
            = this.stockService.getStockStartingWith(ticker.toUpperCase())

}