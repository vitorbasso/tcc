package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
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
import javax.validation.Valid

@RestController
@RequestMapping("\${api-version}/clients")
class ClientController(
        val clientService: IClientService
) {

    @GetMapping
    fun getClient() = this.clientService.getClient()

    @PostMapping
    fun saveClient(@RequestBody @Valid clientRequest: ClientRequest)
            = this.clientService.saveClient(clientRequest)

    @PutMapping
    fun updateClient(@RequestBody @Valid clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(clientUpdateRequest)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteClient(){
        this.clientService.deleteClient()
    }

    @GetMapping("/wallets")
    fun getWallets() = this.clientService.getWalletCollection()

    @GetMapping("/wallets/{broker}")
    fun getWallet(@PathVariable broker: String) = this.clientService.getWallet(broker)

    @PostMapping("/wallets")
    fun saveWallet(@RequestBody walletRequest: WalletRequest)= this.clientService.saveWallet(walletRequest)

    @PutMapping("/wallets/{broker}")
    fun updateWallet(@PathVariable broker: String, @RequestBody walletUpdateRequest: WalletUpdateRequest)
            = this.clientService.updateWallet(broker, walletUpdateRequest)

    @DeleteMapping("/wallets/{broker}")
    fun deleteWallet(@PathVariable broker: String) = this.clientService.deleteWallet(broker)

}