package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IStockRepository
import org.springframework.stereotype.Service

@Service
class StockService(
    private val stockRepository: IStockRepository
) {

    fun getStock(ticker: String) = this.stockRepository.findByTicker(ticker)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun getStockStartingWith(ticker: String) = this.stockRepository.findByTickerStartsWith(ticker)

}