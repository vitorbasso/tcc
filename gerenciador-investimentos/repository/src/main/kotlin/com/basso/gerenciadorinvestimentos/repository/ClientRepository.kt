package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, String> {

}