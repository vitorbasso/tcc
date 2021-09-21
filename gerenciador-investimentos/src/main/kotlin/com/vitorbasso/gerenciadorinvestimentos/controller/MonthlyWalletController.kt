package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/\${api-version}/monthly-wallets")
class MonthlyWalletController(
    private val monthlyWalletService: IMonthlyWalletService
) {

    @GetMapping
    fun getMonthlyWallets() = this.monthlyWalletService.getMonthlyWallets()

    @GetMapping("/{id}")
    fun getMonthlyWallet(@PathVariable id: Long) = this.monthlyWalletService.getMonthlyWallet(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMonthlyWallet(@PathVariable id: Long) = this.monthlyWalletService.deleteMonthlyWallet(id)

}