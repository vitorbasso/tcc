package com.basso.gerenciadorinvestimentos.domain

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Stock (
        @Id
        val symbol: String,
        val currentValue: java.math.BigDecimal,
        val type: Char,
        val name: String,
        val corporation: String,
        val businessArea: String,

        @OneToMany(mappedBy = "stock")
        val stockAssets: MutableList<StockAssets> = mutableListOf()
)