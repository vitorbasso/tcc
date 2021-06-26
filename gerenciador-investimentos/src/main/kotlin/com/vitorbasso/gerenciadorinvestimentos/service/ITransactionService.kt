package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest

interface ITransactionService {
    fun performTransaction(transactionsRequest: List<TransactionRequest>): List<ITransaction>

    fun deleteTransaction(transactionId: Long)
}