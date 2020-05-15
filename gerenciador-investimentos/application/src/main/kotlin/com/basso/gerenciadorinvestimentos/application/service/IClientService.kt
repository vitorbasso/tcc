package com.basso.gerenciadorinvestimentos.application.service

import com.basso.gerenciadorinvestimentos.application.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.domain.IClient

interface IClientService {

    fun getClient(cpf: String): IClient

    fun saveClient(clientRequest: IClient) : IClient

    fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest) : IClient

    fun deleteClient(cpf: String)

}