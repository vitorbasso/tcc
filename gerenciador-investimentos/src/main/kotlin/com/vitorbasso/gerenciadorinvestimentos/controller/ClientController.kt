package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun getClient(@AuthenticationPrincipal clientDetails: Client)
            = this.clientService.getClient(clientDetails.id)

    @PostMapping
    fun saveClient(@RequestBody @Valid clientRequest: ClientRequest)
            = this.clientService.saveClient(clientRequest)

    @PutMapping
    fun updateClient(
            @AuthenticationPrincipal clientDetails: Client,
            @RequestBody @Valid clientUpdateRequest: ClientUpdateRequest
    )
            = this.clientService.updateClient(clientDetails.id, clientUpdateRequest)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteClient(@AuthenticationPrincipal clientDetails: Client){
        this.clientService.deleteClient(clientDetails.id)
    }

    @GetMapping("/wallets")
    fun getWallets(@AuthenticationPrincipal clientDetails: Client)
            = this.clientService.getWalletCollection(clientDetails.id)

    @GetMapping("/wallets/{broker}")
    fun getWallet(
            @AuthenticationPrincipal clientDetails: Client,
            @PathVariable broker: String
    )
            = this.clientService.getWallet(clientDetails.id, broker)

    @PostMapping("/wallets")
    fun saveWallet(
            @AuthenticationPrincipal clientDetails: Client,
            @RequestBody walletRequest: WalletRequest
    )
            = this.clientService.saveWallet(clientDetails.id, walletRequest)

    @PutMapping("/wallets/{broker}")
    fun updateWallet(
            @AuthenticationPrincipal clientDetails: Client,
            @PathVariable broker: String,
            @RequestBody walletUpdateRequest: WalletUpdateRequest
    ) = this.clientService.updateWallet(
            id = clientDetails.id,
            broker = broker,
            walletUpdateRequest = walletUpdateRequest
    )

    @DeleteMapping("/wallets/{broker}")
    fun deleteWallet(@AuthenticationPrincipal clientDetails: Client, @PathVariable broker: String)
            = this.clientService.deleteWallet(clientDetails.id, broker)

}