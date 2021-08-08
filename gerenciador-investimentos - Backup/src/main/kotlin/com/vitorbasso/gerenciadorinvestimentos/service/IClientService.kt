package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest

interface IClientService {

    fun getClient(): IClient

    fun saveClient(clientToSave: IClient): IClient

    fun updateClient(clientUpdateRequest: ClientUpdateRequest): IClient

    fun deleteClient()

}