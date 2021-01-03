package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest

interface ITransactionService {
    fun performTransaction(transactionRequest: TransactionRequest) : ITransaction

    fun deleteTransaction(transactionId: Long)
}