package com.basso.gerenciadorinvestimentos.application.service.concrete

import com.basso.gerenciadorinvestimentos.repository.TransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(
        val transactionRepository: TransactionRepository
) {

}