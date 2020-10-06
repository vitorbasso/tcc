package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(
        val transactionRepository: ITransactionRepository
)