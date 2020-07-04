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
@RequestMapping("/\${api-version}/clients")
class ClientController(
        val clientService: IClientService
) {

    @GetMapping("/{cpf}")
    fun getClient(@PathVariable cpf: String)
            = this.clientService.getClient(cpf)

    @PostMapping
    fun saveClient(@RequestBody @Valid clientRequest: ClientRequest)
            = this.clientService.saveClient(clientRequest)

    @PutMapping("/{cpf}")
    fun updateClient(@PathVariable cpf: String, @RequestBody @Valid clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(cpf, clientUpdateRequest)

    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteClient(@PathVariable cpf: String){
        this.clientService.deleteClient(cpf)
    }

    @GetMapping("/{cpf}/wallets")
    fun getWallets(@PathVariable cpf: String)
            = this.clientService.getWalletCollection(cpf)

    @GetMapping("/{cpf}/wallets/{broker}")
    fun getWallet(@PathVariable cpf: String, @PathVariable broker: String)
            = this.clientService.getWallet(cpf, broker)

    @PostMapping("/{cpf}/wallets")
    fun saveWallet(@PathVariable cpf: String, @RequestBody walletRequest: WalletRequest)
            = this.clientService.saveWallet(cpf, walletRequest)

    @PutMapping("/{cpf}/wallets/{broker}")
    fun updateWallet(
            @PathVariable cpf: String,
            @PathVariable broker: String,
            @RequestBody walletUpdateRequest: WalletUpdateRequest
    ) = this.clientService.updateWallet(
            cpf = cpf,
            broker = broker,
            walletUpdateRequest = walletUpdateRequest
    )

    @DeleteMapping("/{cpf}/wallets/{broker}")
    fun deleteWallet(@PathVariable cpf: String, @PathVariable broker: String)
            = this.clientService.deleteWallet(cpf, broker)

}