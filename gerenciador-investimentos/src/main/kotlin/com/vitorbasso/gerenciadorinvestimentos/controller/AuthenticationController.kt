package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.AuthenticationRequest
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${api-version}/authentication")
class AuthenticationController(
        private val authenticationService: AuthenticationService
) {

    @PostMapping
    fun authenticate(@RequestBody authenticationRequest: AuthenticationRequest)
            = this.authenticationService.authenticate(authenticationRequest)

}