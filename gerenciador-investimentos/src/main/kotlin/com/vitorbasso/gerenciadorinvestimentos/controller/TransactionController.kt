package com.vitorbasso.gerenciadorinvestimentos.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/transactions")
class TransactionController {

    @GetMapping
    fun getTransactionsByWallet() {
        TODO()
    }

}