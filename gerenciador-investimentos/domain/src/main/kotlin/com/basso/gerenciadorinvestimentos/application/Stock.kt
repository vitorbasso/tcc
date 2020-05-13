package com.basso.gerenciadorinvestimentos.application

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Stock (
        @Id
        val symbol: String,
        val currentValue: Long,
        val type: Char,
        val name: String,
        val corporation: String,
        val businessArea: String,

        @OneToMany(mappedBy = "stock")
        val stockAssets: StockAssets
)