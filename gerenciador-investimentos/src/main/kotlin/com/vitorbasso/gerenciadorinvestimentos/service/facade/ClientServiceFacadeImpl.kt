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

    override fun getClient(id: Long) = this.clientService.getClient(id)

    override fun saveClient(clientToSave: IClient)= this.clientService.saveClient(clientToSave as Client)

    override fun updateClient(id: Long, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(
            this.clientService.getClient(
                    id,
                    CustomBadRequestException(ManagerErrorCode.MANAGER_05)
            ),
            clientUpdateRequest
    )

    override fun deleteClient(id: Long)
            = this.clientService.deleteClient(
                    this.clientService.getClient(
                            id,
                            CustomBadRequestException(ManagerErrorCode.MANAGER_06)
                    )
            )

    override fun getWalletCollection(id: Long) = this.clientService.getClient(id).wallet

    override fun getWallet(id: Long, broker: String) = this.walletService.getWallet(id, broker)

    override fun saveWallet(id: Long, walletToSave: IWallet)
            = this.walletService.saveWallet(
            this.clientService.getClient(
                    id,
                    CustomBadRequestException(ManagerErrorCode.MANAGER_08)
            ),
            walletToSave as Wallet
    )

    override fun updateWallet(id: Long, broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(
            this.walletService.getWallet(
                    id = id,
                    broker = broker,
                    exception = CustomBadRequestException(ManagerErrorCode.MANAGER_09)
            ),
            walletUpdateRequest
    )

    override fun deleteWallet(id: Long, broker: String) {
        this.walletService.deleteWallet(
                this.walletService.getWallet(
                        id = id,
                        broker = broker,
                        exception = CustomBadRequestException(ManagerErrorCode.MANAGER_10)
                )
        )
    }
}