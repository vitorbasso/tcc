package com.basso.gerenciamentoinvestimento.repository

import com.basso.gerenciadorinvestimentos.application.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, String>