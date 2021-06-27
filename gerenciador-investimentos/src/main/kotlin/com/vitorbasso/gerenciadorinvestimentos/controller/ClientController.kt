package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/\${api-version}/clients")
class ClientController(
    val clientService: IClientService,
    val walletService: IWalletService
) {

    @GetMapping
    fun getClient() = this.clientService.getClient()

    @PostMapping
    fun saveClient(@RequestBody @Valid clientRequest: ClientRequest) = this.clientService.saveClient(clientRequest)

    @PutMapping
    fun updateClient(@RequestBody @Valid clientUpdateRequest: ClientUpdateRequest) =
        this.clientService.updateClient(clientUpdateRequest)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteClient() {
        this.clientService.deleteClient()
    }

    @GetMapping("/wallets")
    fun getWallet(@RequestParam month: String?): Any = when (month) {
        "all" -> this.walletService.getAllWallets()
        null -> this.walletService.getWallet()
        else -> this.walletService.getWallet(LocalDate.parse(month).atStartOfMonth())
    }

}