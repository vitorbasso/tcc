package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IClientRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
internal class ClientService(
    private val clientRepository: IClientRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getClient(
        id: Long,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = clientRepository.findByIdOrNull(id) ?: throw exception

    fun saveClient(clientToSave: Client) = if (!exists(clientToSave.email))
        this.clientRepository.save(clientToSave.copy(password = this.passwordEncoder.encode(clientToSave.password)))
    else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateClient(clientToUpdate: Client, updateRequest: ClientUpdateRequest) = this.clientRepository.save(
        clientToUpdate.copy(
            name = updateRequest.name ?: clientToUpdate.name
        )
    )

    fun deleteClient(clientToDelete: Client) {
        this.clientRepository.delete(clientToDelete)
    }

    private fun exists(email: String) = this.clientRepository.existsByEmail(email)

}
