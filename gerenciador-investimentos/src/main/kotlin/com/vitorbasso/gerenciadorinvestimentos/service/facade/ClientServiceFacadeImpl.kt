package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import org.springframework.stereotype.Service

@Service
internal class ClientServiceFacadeImpl(
        private val clientService: ClientService,
        private val walletService: WalletService
) : IClientService {

    override fun getClient(cpf: String) = this.clientService.getClient(cpf)

    override fun saveClient(clientToSave: IClient)= this.clientService.saveClient(clientToSave as Client)

    override fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(
            this.clientService.getClient(
                    cpf,
                    CustomBadRequestException(ManagerErrorCode.MANAGER_05)
            ),
            clientUpdateRequest
    )

    override fun deleteClient(cpf: String)
            = this.clientService.deleteClient(
                    this.clientService.getClient(
                            cpf,
                            CustomBadRequestException(ManagerErrorCode.MANAGER_06)
                    )
            )

    override fun getWalletCollection(cpf: String) = this.clientService.getClient(cpf).wallet

    override fun getWallet(cpf: String, broker: String) = this.walletService.getWallet(cpf, broker)

    override fun saveWallet(cpf: String, walletToSave: IWallet)
            = this.walletService.saveWallet(
            this.clientService.getClient(
                    cpf,
                    CustomBadRequestException(ManagerErrorCode.MANAGER_08)
            ),
            walletToSave as Wallet
    )

    override fun updateWallet(cpf: String, broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(
            this.walletService.getWallet(
                    cpf = cpf,
                    broker = broker,
                    exception = CustomBadRequestException(ManagerErrorCode.MANAGER_09)
            ),
            walletUpdateRequest
    )

    override fun deleteWallet(cpf: String, broker: String) {
        this.walletService.deleteWallet(
                this.walletService.getWallet(
                        cpf = cpf,
                        broker = broker,
                        exception = CustomBadRequestException(ManagerErrorCode.MANAGER_10)
                )
        )
    }
}