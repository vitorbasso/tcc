package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.proxy.MonthlyWalletServiceProxy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/monthly-wallet")
class MonthlyWalletController(
    private val monthlyWalletService: MonthlyWalletServiceProxy
) {

    @GetMapping
    fun getMonthlyWallets() = this.monthlyWalletService.getMonthlyWallets()

    @GetMapping("/{id}")
    fun getMonthlyWallet(@PathVariable id: Long) = this.monthlyWalletService.getMonthlyWallet(id)

}