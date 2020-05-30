package com.basso.gerenciadorinvestimentos.application.service.facade

import com.basso.gerenciadorinvestimentos.application.dto.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.dto.request.WalletUpdateRequest
import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode
import com.basso.gerenciadorinvestimentos.application.exception.CustomBadRequestException
import com.basso.gerenciadorinvestimentos.application.service.IClientService
import com.basso.gerenciadorinvestimentos.application.service.concrete.ClientService
import com.basso.gerenciadorinvestimentos.application.service.concrete.WalletService
import com.basso.gerenciadorinvestimentos.domain.IClient
import com.basso.gerenciadorinvestimentos.domain.IWallet
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import com.basso.gerenciadorinvestimentos.domain.concrete.Wallet
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