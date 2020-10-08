package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.AuthenticationRequest
import com.vitorbasso.gerenciadorinvestimentos.service.security.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/\${api-version}/authentication")
class AuthenticationController(
        private val authenticationService: AuthenticationService
) {

    @PostMapping
    fun authenticate(@RequestBody @Valid authenticationRequest: AuthenticationRequest)
            = this.authenticationService.authenticate(authenticationRequest)

}