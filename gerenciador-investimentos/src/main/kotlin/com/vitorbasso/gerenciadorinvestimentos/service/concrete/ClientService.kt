package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.ClientRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
internal class ClientService (
        private val clientRepository: ClientRepository,
        private val passwordEncoder: PasswordEncoder
) {

    fun getClient(
            cpf: String,
            exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    )
            = clientRepository.findByCpf(cpf) ?: throw exception

    fun saveClient(clientToSave: Client)
            = if(!exists(clientToSave.cpf))
        this.clientRepository.save(clientToSave.copy(password = this.passwordEncoder.encode(clientToSave.password)))
        else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateClient(clientToUpdate: Client, updateRequest: ClientUpdateRequest)
            = this.clientRepository.save(clientToUpdate.copy(
            firstName = updateRequest.firstName ?: clientToUpdate.firstName,
            lastName = updateRequest.lastName ?: clientToUpdate.lastName,
            avatarImage = updateRequest.avatarImage ?: clientToUpdate.avatarImage
    ))

    fun deleteClient(clientToDelete: Client) {
        this.clientRepository.delete(clientToDelete)
    }

    private fun exists(cpf: String) = this.clientRepository.existsByCpf(cpf)

}