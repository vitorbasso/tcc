package com.vitorbasso.gerenciadorinvestimentos.filter.security

import com.vitorbasso.gerenciadorinvestimentos.service.security.ClientDetailsService
import com.vitorbasso.gerenciadorinvestimentos.util.JwtUtil
import io.jsonwebtoken.Claims
import io.jsonwebtoken.MalformedJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilter(
        private val clientDetailsService: ClientDetailsService,
        private val jwtUtil: JwtUtil
) : OncePerRequestFilter(){

    private val tokenPrefix = "Bearer "
    private val requestHeader = "Authorization"

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authenticationHeader : String? = request.getHeader(this.requestHeader)

        if(!authenticationHeader.isNullOrBlank() && authenticationHeader.startsWith(this.tokenPrefix)) {

            val tokenClaims = try {
                this.jwtUtil.getTokenBody(authenticationHeader.replace(this.tokenPrefix, ""))
            }catch (ex: MalformedJwtException) {
                null
            }

            if (this.jwtUtil.isTokenValid(tokenClaims)) {
                attemptAuthentication(tokenClaims!!, request)
            }

        }
        chain.doFilter(request, response)
    }


    private fun attemptAuthentication(tokenClaims: Claims, request: HttpServletRequest) {
        val userDetails = this.clientDetailsService.loadUserByUsername(tokenClaims.subject)
        if (userDetails != null) {
            val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
            )
            usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
    }

}