package com.vitorbasso.gerenciadorinvestimentos.config

import com.vitorbasso.gerenciadorinvestimentos.filter.security.AuthenticationFilter
import com.vitorbasso.gerenciadorinvestimentos.filter.security.RestAuthenticationEntryPoint
import com.vitorbasso.gerenciadorinvestimentos.service.security.ClientDetailsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${api-version}")
    private val apiVersion: String,
    private val clientDetailsService: ClientDetailsService,
    private val authenticationFilter: AuthenticationFilter,
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(this.clientDetailsService).passwordEncoder(passwordEncoder())
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "${apiVersion}/clients").permitAll()
            .antMatchers("${apiVersion}/authentication").permitAll()
            .anyRequest().authenticated()
            .and().exceptionHandling().authenticationEntryPoint(this.restAuthenticationEntryPoint)
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManager()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

}