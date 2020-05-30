package com.basso.gerenciadorinvestimentos.domain.concrete

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Stock (
        @Id
        val symbol: String,
        val currentValue: java.math.BigDecimal,
        val type: Char,
        val name: String,
        val corporation: String,
        val businessArea: String

) : Serializable