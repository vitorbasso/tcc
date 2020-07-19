package com.vitorbasso.gerenciadorinvestimentos.util

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomForbiddenException
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

    private fun getTokenBody(token: String) = try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).body
        } catch (ex: Exception) {
            throw CustomForbiddenException(ManagerErrorCode.MANAGER_09)
        }

    private fun generateToken(claims: Map<String, Any>, userDetails: UserDetails)
            = Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + this.expirationTime))
            .signWith(SignatureAlgorithm.HS512, this.secret).compact()

}