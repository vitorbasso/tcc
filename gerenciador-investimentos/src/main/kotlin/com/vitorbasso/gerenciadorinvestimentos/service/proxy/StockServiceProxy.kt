package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.response.StockListDto
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import org.springframework.stereotype.Service

@Service
class StockServiceProxy(
        private val stockService: StockService
): IStockService {

    override fun getStock(ticker: String) = this.stockService.getStock(ticker)

    override fun getStockStartingWith(ticker: String)
            = getListDto(this.stockService.getStockStartingWith(ticker))

    private fun getListDto(tickers: List<Stock>) = StockListDto(tickers.map { it.ticker })

}