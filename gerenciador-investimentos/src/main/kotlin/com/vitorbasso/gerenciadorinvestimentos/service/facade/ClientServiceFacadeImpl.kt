package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service

@Service
internal class ClientServiceFacadeImpl(
    private val clientService: ClientService
) : IClientService {

    override fun getClient() = SecurityContextUtil.getClientDetails()

    override fun saveClient(clientToSave: IClient) = this.clientService.saveClient(clientToSave as Client)

    override fun updateClient(clientUpdateRequest: ClientUpdateRequest) = this.clientService.updateClient(
        getClient(),
        clientUpdateRequest
    )

    override fun deleteClient() = this.clientService.deleteClient(getClient())

}