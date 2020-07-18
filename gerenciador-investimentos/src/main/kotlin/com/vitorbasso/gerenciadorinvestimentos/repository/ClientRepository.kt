package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, String> {
    fun findByCpf(cpf: String) : Client?

    fun findByEmail(email: String) : Client?

    fun existsByCpf(cpf: String) : Boolean
}