package com.basso.gerenciadorinvestimentos.application.service.facade

import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode
import com.basso.gerenciadorinvestimentos.application.exception.CustomBadRequestException
import com.basso.gerenciadorinvestimentos.application.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.service.IClientService
import com.basso.gerenciadorinvestimentos.application.service.concrete.ClientService
import com.basso.gerenciadorinvestimentos.domain.IClient
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import org.springframework.stereotype.Service

@Service
internal class ClientServiceFacade(
        private val clientService: ClientService
) : IClientService {

    override fun getClient(cpf: String) = this.clientService.getClient(cpf)

    override fun saveClient(clientRequest: IClient)= this.clientService.saveClient(clientRequest as Client)

    override fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(
            this.clientService.getClient(
                    cpf,
                    CustomBadRequestException(ManagerErrorCode.MANAGER_06)
            ),
            clientUpdateRequest
    )

    override fun deleteClient(cpf: String)
            = this.clientService.deleteClient(
                    this.clientService.getClient(
                            cpf,
                            CustomBadRequestException(ManagerErrorCode.MANAGER_07)
                    )
            )
}