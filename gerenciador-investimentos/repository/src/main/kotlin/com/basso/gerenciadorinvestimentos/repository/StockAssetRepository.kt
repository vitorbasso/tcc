package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.concrete.StockAsset
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockAssetRepository : JpaRepository<StockAsset, Int>