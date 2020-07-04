package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.repository.TransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(
        val transactionRepository: TransactionRepository
) {

}