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
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
internal class ClientServiceFacadeImpl(
        private val clientService: ClientService,
        private val walletService: WalletService
) : IClientService {

    override fun getClient() = getClientDetails()

    override fun saveClient(clientToSave: IClient)= this.clientService.saveClient(clientToSave as Client)

    override fun updateClient(clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(
            getClientDetails(),
            clientUpdateRequest
    )

    override fun deleteClient()
            = this.clientService.deleteClient(getClientDetails())

    override fun getWalletCollection()
            = this.clientService.getClient(getClientDetails().id).wallet

    override fun getWallet(broker: String)
            = this.walletService.getWallet(getClientDetails().id, broker)

    override fun saveWallet(walletToSave: IWallet)
            = this.walletService.saveWallet(
            getClientDetails(),
            walletToSave as Wallet
    )

    override fun updateWallet(broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(
            this.walletService.getWallet(
                    id = getClientDetails().id,
                    broker = broker,
                    exception = CustomBadRequestException(ManagerErrorCode.MANAGER_09)
            ),
            walletUpdateRequest
    )

    override fun deleteWallet(broker: String) {
        this.walletService.deleteWallet(
                this.walletService.getWallet(
                        id = getClientDetails().id,
                        broker = broker,
                        exception = CustomBadRequestException(ManagerErrorCode.MANAGER_10)
                )
        )
    }

    private fun getClientDetails() = SecurityContextHolder.getContext().authentication.principal as Client
}