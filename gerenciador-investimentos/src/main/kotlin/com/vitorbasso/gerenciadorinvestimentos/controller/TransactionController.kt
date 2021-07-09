package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/transactions")
class TransactionController(
    private val transactionService: ITransactionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun saveTransactions(@RequestBody transactionsRequest: List<TransactionRequest>) {
        this.transactionService.performTransaction(transactionsRequest)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTransaction(@PathVariable id: Long) {
        this.transactionService.deleteTransaction(id)
    }

}