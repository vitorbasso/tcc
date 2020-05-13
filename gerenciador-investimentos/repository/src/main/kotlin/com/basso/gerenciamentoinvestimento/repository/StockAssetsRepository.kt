package com.basso.gerenciamentoinvestimento.repository

import com.basso.gerenciadorinvestimentos.application.StockAssets
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockAssetsRepository : JpaRepository<StockAssets, Int>