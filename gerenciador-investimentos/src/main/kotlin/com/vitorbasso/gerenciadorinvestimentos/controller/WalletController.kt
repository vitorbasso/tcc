package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/wallets")
class WalletController(
        private val walletService: IWalletService
) {

    @GetMapping
    fun getWallets() = this.walletService.getWalletCollection()

    @GetMapping("/{broker}")
    fun getWallet(@PathVariable broker: String) = this.walletService.getWallet(broker)

    @PostMapping
    fun saveWallet(@RequestBody walletRequest: WalletRequest)= this.walletService.saveWallet(walletRequest)

    @PutMapping("/{broker}")
    fun updateWallet(@PathVariable broker: String, @RequestBody walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(broker, walletUpdateRequest)

    @DeleteMapping("/{broker}")
    fun deleteWallet(@PathVariable broker: String) = this.walletService.deleteWallet(broker)

}