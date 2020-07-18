package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.repository.ClientRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class ClientDetailsService(
        private val clientRepository: ClientRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String) = this.clientRepository.findByEmail(username)
}