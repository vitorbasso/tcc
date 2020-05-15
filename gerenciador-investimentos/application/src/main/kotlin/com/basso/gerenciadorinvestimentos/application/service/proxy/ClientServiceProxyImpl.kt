package com.basso.gerenciadorinvestimentos.application.service.proxy

import com.basso.gerenciadorinvestimentos.application.dto.ClientDto
import com.basso.gerenciadorinvestimentos.application.request.ClientRequest
import com.basso.gerenciadorinvestimentos.application.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.service.IClientService
import com.basso.gerenciadorinvestimentos.domain.IClient
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import com.basso.gerenciadorinvestimentos.domain.concrete.User
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class ClientServiceProxyImpl (
        @Qualifier("clientServiceFacade")
        private val clientService: IClientService
) : IClientService {

    override fun getClient(cpf: String) = this.clientService.getClient(cpf).getDto()

    override fun saveClient(clientRequest: IClient)
            = this.clientService.saveClient(clientRequest.getEntity()).getDto()

    override fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(cpf, clientUpdateRequest).getDto()

    override fun deleteClient(cpf: String) {
        this.clientService.deleteClient(cpf)
    }
}

private fun IClient.getDto() = ClientDto(
        firstName = (this as Client).firstName,
        lastName = this.lastName,
        email = this.email,
        cpf = this.cpf,
        avatarImage = this.avatarImage
)

private fun IClient.getEntity() = Client(
        email = (this as ClientRequest).email,
        cpf = this.cpf,
        firstName = this.firstName,
        lastName = this.lastName,
        avatarImage = this.avatarImage,
        user = User(password = this.password)
)