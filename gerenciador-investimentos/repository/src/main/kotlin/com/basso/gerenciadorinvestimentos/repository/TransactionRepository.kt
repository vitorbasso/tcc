package com.basso.gerenciadorinvestimentos.repository

import com.basso.gerenciadorinvestimentos.domain.concrete.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long>