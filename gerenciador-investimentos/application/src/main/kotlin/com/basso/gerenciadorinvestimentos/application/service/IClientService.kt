package com.basso.gerenciadorinvestimentos.application.service

import com.basso.gerenciadorinvestimentos.application.dto.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.dto.request.WalletUpdateRequest
import com.basso.gerenciadorinvestimentos.domain.IClient
import com.basso.gerenciadorinvestimentos.domain.IWallet

interface IClientService {

    fun getClient(cpf: String): IClient

    fun saveClient(clientToSave: IClient) : IClient

    fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest) : IClient

    fun deleteClient(cpf: String)

    fun getWalletCollection(cpf: String) : List<IWallet>

    fun getWallet(cpf: String, broker: String) : IWallet

    fun saveWallet(cpf: String, walletToSave: IWallet) : IWallet

    fun updateWallet(cpf: String, broker: String, walletUpdateRequest: WalletUpdateRequest) : IWallet

    fun deleteWallet(cpf: String, broker: String)

}