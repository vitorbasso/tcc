package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/transactions")
class TransactionController(
    private val transactionService: ITransactionService
) {

    @PostMapping
    fun getTransactionsByWallet(@RequestBody transactionRequest: TransactionRequest)
        = this.transactionService.performTransaction(transactionRequest)

}