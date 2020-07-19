package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest

interface IClientService {

    fun getClient(id: Long): IClient

    fun saveClient(clientToSave: IClient) : IClient

    fun updateClient(id: Long, clientUpdateRequest: ClientUpdateRequest) : IClient

    fun deleteClient(id: Long)

    fun getWalletCollection(id: Long) : List<IWallet>

    fun getWallet(id: Long, broker: String) : IWallet

    fun saveWallet(id: Long, walletToSave: IWallet) : IWallet

    fun updateWallet(id: Long, broker: String, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(id: Long, broker: String)

}