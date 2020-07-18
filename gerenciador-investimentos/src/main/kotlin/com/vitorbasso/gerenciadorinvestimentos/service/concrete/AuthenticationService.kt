package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.AuthenticationRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.AuthenticationDto
import com.vitorbasso.gerenciadorinvestimentos.util.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
        private val jwtUtil: JwtUtil,
        private val authenticationManager: AuthenticationManager
) {

    fun authenticate(authenticationRequest: AuthenticationRequest) = this.authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password)
    ).let {
        AuthenticationDto(this.jwtUtil.generateToken(it.principal as Client))
    }

}