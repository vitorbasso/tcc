package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest

interface IClientService {

    fun getClient(): IClient

    fun saveClient(clientToSave: IClient) : IClient

    fun updateClient(clientUpdateRequest: ClientUpdateRequest) : IClient

    fun deleteClient()

    fun getWalletCollection() : List<IWallet>

    fun getWallet(broker: String) : IWallet

    fun saveWallet(walletToSave: IWallet) : IWallet

    fun updateWallet(broker: String, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(broker: String)

}