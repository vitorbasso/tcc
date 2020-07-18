package com.vitorbasso.gerenciadorinvestimentos.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
        private val passwordEncoder: PasswordEncoder,
        @Value("\${gerenciador-investimento.security.secret:secret}")
        private val secret: String,
        @Value("\${gerenciador-investimento.security.expiration-time}")
        private val expirationTime: Long
) {

    fun generateToken(userDetails: UserDetails) = getToken(hashMapOf(), userDetails).also { println(this.secret) }

    private fun getToken(claims: Map<String, Any>, userDetails: UserDetails)
            = Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(SignatureAlgorithm.HS512, this.secret).compact()

}