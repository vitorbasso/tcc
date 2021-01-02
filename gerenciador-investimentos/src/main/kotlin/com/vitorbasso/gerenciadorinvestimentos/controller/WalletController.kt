package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/wallets")
class WalletController(
        private val walletService: IWalletService
) {

    @GetMapping
    fun getWallets() = this.walletService.getWalletCollection()

    @GetMapping("/{id}")
    fun getWallet(@PathVariable id: Long) = this.walletService.getWallet(id)

    @PostMapping
    fun saveWallet(@RequestBody walletRequest: WalletRequest)= this.walletService.saveWallet(walletRequest)

    @PutMapping("/{id}")
    fun updateWallet(@PathVariable id: Long, @RequestBody walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(id, walletUpdateRequest)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteWallet(@PathVariable id: Long) = this.walletService.deleteWallet(id)

}