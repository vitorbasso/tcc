package com.basso.gerenciamentoinvestimento.repository

import com.basso.gerenciadorinvestimentos.application.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, String>