package com.vitorbasso.gerenciadorinvestimentos.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
        @Value("\${gerenciador-investimento.security.secret:secret}")
        private val secret: String,
        @Value("\${gerenciador-investimento.security.expiration-time}")
        private val expirationTime: Long
) {

    fun generateToken(userDetails: UserDetails) = generateToken(hashMapOf(), userDetails)

    fun getSubject(token: String) = getTokenBody(token).subject

    fun isTokenExpired(token: String) = getTokenBody(token).expiration.before(Date())

    private fun getTokenBody(token: String) = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).body

    private fun generateToken(claims: Map<String, Any>, userDetails: UserDetails)
            = Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + this.expirationTime))
            .signWith(SignatureAlgorithm.HS512, this.secret).compact()

}