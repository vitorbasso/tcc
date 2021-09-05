package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletDto
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IStockRepository
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: IStockRepository,
    private val walletService: IWalletService
) {

    fun getStock(ticker: String) = this.stockRepository.findByTicker(ticker)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getStocksBatch(tickers: List<String>) = this.stockRepository.findByTickerBatch(tickers)

    fun getAllStocks(): List<Stock> {
        val symbols = (walletService.getWallet() as WalletDto).stockAssets.map { it.stockSymbol }
        return getStocksBatch(symbols)
    }

    fun getStockStartingWith(ticker: String) = this.stockRepository.findByTickerStartsWith(ticker)

}