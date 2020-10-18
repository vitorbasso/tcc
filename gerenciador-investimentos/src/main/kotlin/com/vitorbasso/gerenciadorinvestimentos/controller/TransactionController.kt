package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/transactions")
class TransactionController (
    private val assetService: AssetService
) {

    @GetMapping
    fun getTransactionsByWallet() {
        TODO()
    }

}