package com.vitorbasso.gerenciadorinvestimentos.service.security

import com.vitorbasso.gerenciadorinvestimentos.repository.IClientRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class ClientDetailsService(
    private val clientRepository: IClientRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String) = this.clientRepository.findByEmail(username)
}