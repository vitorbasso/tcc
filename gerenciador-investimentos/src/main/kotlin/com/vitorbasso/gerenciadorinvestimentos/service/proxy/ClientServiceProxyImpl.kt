package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.ClientDto
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class ClientServiceProxyImpl(
    @Qualifier("clientServiceFacadeImpl")
    private val clientService: IClientService
) : IClientService {

    override fun getClient() = this.clientService.getClient().getDto()

    override fun saveClient(clientToSave: IClient) = this.clientService.saveClient(clientToSave.getEntity()).getDto()

    override fun updateClient(clientUpdateRequest: ClientUpdateRequest) =
        this.clientService.updateClient(clientUpdateRequest).getDto()

    override fun deleteClient() {
        this.clientService.deleteClient()
    }

}

private fun IClient.getDto() = ClientDto(
    id = (this as Client).id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    cpf = this.cpf,
    avatarImage = this.avatarImage
)

private fun IClient.getEntity() = Client(
    cpf = (this as ClientRequest).cpf,
    email = this.email,
    password = this.password,
    firstName = this.firstName,
    lastName = this.lastName,
    avatarImage = this.avatarImage
)