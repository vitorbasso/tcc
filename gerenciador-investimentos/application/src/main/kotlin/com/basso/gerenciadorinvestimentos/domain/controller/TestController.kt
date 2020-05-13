package com.basso.gerenciadorinvestimentos.domain.controller

import com.basso.gerenciadorinvestimentos.domain.service.ClientService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api-version}/hello")
class TestController (
        val clientService: ClientService
) {

    @GetMapping
    fun helloThere() = "Hello There!"

    @GetMapping("/general")
    fun helloGeneral() = "...General Kenobi..."

    @GetMapping("/clients")
    fun getClients() = clientService.getClients()

}