package com.basso.gerenciadorinvestimentos.domain.controller

import com.basso.gerenciadorinvestimentos.domain.service.ClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api-version}/clients")
class TestController (
        val clientService: ClientService
) {

    @GetMapping
    fun getClients() = clientService.getClients()

}