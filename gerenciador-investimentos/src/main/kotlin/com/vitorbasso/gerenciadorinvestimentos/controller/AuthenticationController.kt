package com.vitorbasso.gerenciadorinvestimentos.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api-version}/authentication")
class AuthenticationController {

    @PostMapping
    fun authenticate() {}

}