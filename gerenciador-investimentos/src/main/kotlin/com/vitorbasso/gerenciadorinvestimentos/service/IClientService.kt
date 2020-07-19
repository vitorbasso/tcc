package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest

interface IClientService {

    fun getClient(client: IClient): IClient

    fun saveClient(clientToSave: IClient) : IClient

    fun updateClient(client: IClient, clientUpdateRequest: ClientUpdateRequest) : IClient

    fun deleteClient(client: IClient)

    fun getWalletCollection(client: IClient) : List<IWallet>

    fun getWallet(client: IClient, broker: String) : IWallet

    fun saveWallet(client: IClient, walletToSave: IWallet) : IWallet

    fun updateWallet(client: IClient, broker: String, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(client: IClient, broker: String)

}