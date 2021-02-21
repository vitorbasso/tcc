package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import java.time.LocalDate

interface ITaxService {
    fun getTax(month: LocalDate): ITax
}