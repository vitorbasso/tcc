package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.response.StockListDto

interface IStockService {

    fun getStock(ticker: String): Stock

    fun getStocksBatch(tickers: List<String>): List<Stock>

    fun getStockStartingWith(ticker: String): StockListDto

}