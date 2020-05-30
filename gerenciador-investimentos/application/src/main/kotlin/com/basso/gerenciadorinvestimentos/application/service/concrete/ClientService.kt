package com.basso.gerenciadorinvestimentos.application.service.concrete

import com.basso.gerenciadorinvestimentos.application.dto.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode
import com.basso.gerenciadorinvestimentos.application.exception.CustomBadRequestException
import com.basso.gerenciadorinvestimentos.application.exception.CustomEntityNotFoundException
import com.basso.gerenciadorinvestimentos.application.exception.CustomManagerException
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import com.basso.gerenciadorinvestimentos.repository.ClientRepository
import org.springframework.stereotype.Service

@Service
internal class ClientService (
        private val clientRepository: ClientRepository
) {

    fun getClient(
            cpf: String,
            exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    )
            = if(exists(cpf)) clientRepository.findByCpf(cpf) else throw exception

    fun saveClient(clientToSave: Client)
            = if(!exists(clientToSave.cpf)) this.clientRepository.save(clientToSave)
            else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateClient(clientToUpdate: Client, updateRequest: ClientUpdateRequest)
            = this.clientRepository.save(
            Client(
                    email = clientToUpdate.email,
                    cpf = clientToUpdate.cpf,
                    firstName = updateRequest.firstName ?: clientToUpdate.firstName,
                    lastName = updateRequest.lastName ?: clientToUpdate.lastName,
                    avatarImage = updateRequest.avatarImage ?: clientToUpdate.avatarImage,
                    user = clientToUpdate.user,
                    wallet = clientToUpdate.wallet
            )
    )

    fun deleteClient(clientToDelete: Client) {
        this.clientRepository.delete(clientToDelete)
    }

    private fun exists(cpf: String) = this.clientRepository.existsByCpf(cpf)

}