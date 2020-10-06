package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IStock
import com.vitorbasso.gerenciadorinvestimentos.dto.response.StockListDto

interface IStockService {

    fun getStock(ticker: String): IStock

    fun getStockStartingWith(ticker: String): StockListDto

}