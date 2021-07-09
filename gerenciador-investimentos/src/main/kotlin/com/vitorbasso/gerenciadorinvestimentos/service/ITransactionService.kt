package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest

interface ITransactionService {
    fun performTransaction(transactionsRequest: List<TransactionRequest>): Unit

    fun deleteTransaction(transactionId: Long)
}