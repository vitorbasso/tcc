package com.basso.gerenciadorinvestimentos.domain.service

import com.basso.gerenciadorinvestimentos.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
class ClientService (
        val clientRepository: ClientRepository
) {

    fun getClients() = clientRepository.findAll()

}