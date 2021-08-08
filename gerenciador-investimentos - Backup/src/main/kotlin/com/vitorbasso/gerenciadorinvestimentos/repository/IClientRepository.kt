package com.vitorbasso.gerenciadorinvestimentos.repository

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IClientRepository : JpaRepository<Client, Long> {
    fun findByEmail(email: String): Client?

    fun existsByEmail(email: String): Boolean
}