package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.StockAssets
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockAssetsRepository : JpaRepository<StockAssets, Int> {

}