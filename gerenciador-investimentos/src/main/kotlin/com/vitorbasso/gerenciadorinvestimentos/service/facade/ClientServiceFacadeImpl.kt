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

    override fun getClient(client: IClient) = client as Client

    override fun saveClient(clientToSave: IClient)= this.clientService.saveClient(clientToSave as Client)

    override fun updateClient(client: IClient, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(
            (client as Client),
            clientUpdateRequest
    )

    override fun deleteClient(client: IClient)
            = this.clientService.deleteClient((client as Client))

    override fun getWalletCollection(client: IClient)
            = this.clientService.getClient((client as Client).id).wallet

    override fun getWallet(client: IClient, broker: String)
            = this.walletService.getWallet((client as Client).id, broker)

    override fun saveWallet(client: IClient, walletToSave: IWallet)
            = this.walletService.saveWallet(
            (client as Client),
            walletToSave as Wallet
    )

    override fun updateWallet(client: IClient, broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(
            this.walletService.getWallet(
                    id = (client as Client).id,
                    broker = broker,
                    exception = CustomBadRequestException(ManagerErrorCode.MANAGER_09)
            ),
            walletUpdateRequest
    )

    override fun deleteWallet(client: IClient, broker: String) {
        this.walletService.deleteWallet(
                this.walletService.getWallet(
                        id = (client as Client).id,
                        broker = broker,
                        exception = CustomBadRequestException(ManagerErrorCode.MANAGER_10)
                )
        )
    }
}