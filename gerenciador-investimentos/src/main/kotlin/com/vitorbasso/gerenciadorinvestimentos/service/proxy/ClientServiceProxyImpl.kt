package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.IAsset
import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.AssetDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.ClientDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletDto
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletSmallDto
import com.vitorbasso.gerenciadorinvestimentos.service.IClientService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
@Primary
class ClientServiceProxyImpl (
        @Qualifier("clientServiceFacadeImpl")
        private val clientService: IClientService
) : IClientService {

    override fun getClient(client: IClient)
            = this.clientService.getClient(client).getDto()

    override fun saveClient(clientToSave: IClient)
            = this.clientService.saveClient(clientToSave.getEntity()).getDto()

    override fun updateClient(client: IClient, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(client, clientUpdateRequest).getDto()

    override fun deleteClient(client: IClient) {
        this.clientService.deleteClient(client)
    }

    override fun getWalletCollection(client: IClient)
            = this.clientService.getWalletCollection(client).map { it.getSmallDto() }

    override fun getWallet(client: IClient, broker: String)
            = this.clientService.getWallet(client, broker).getDto()

    override fun saveWallet(client: IClient, walletToSave: IWallet)
            = this.clientService.saveWallet(client, walletToSave.getEntity()).getDto()

    override fun updateWallet(client: IClient, broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.clientService.updateWallet(
            client = client,
            broker = broker,
            walletUpdateRequest = walletUpdateRequest
    ).getDto()

    override fun deleteWallet(client: IClient, broker: String) {
        this.clientService.deleteWallet(client, broker)
    }
}

private fun IClient.getDto() = ClientDto(
        firstName = (this as Client).firstName,
        lastName = this.lastName,
        email = this.email,
        cpf = this.cpf,
        avatarImage = this.avatarImage
)

private fun IClient.getEntity() = Client(
        cpf = (this as ClientRequest).cpf,
        email = this.email,
        password = this.password,
        firstName = this.firstName,
        lastName = this.lastName,
        avatarImage = this.avatarImage
)

private fun IWallet.getEntity() = Wallet(
        name = (this as WalletRequest).name,
        broker = this.broker,
        client = SecurityContextHolder.getContext().authentication.principal as Client
)

private fun IWallet.getDto() = WalletDto(
        name = (this as Wallet).name,
        broker = this.broker,
        lossDaytrade = this.lossDaytrade,
        loss = this.loss,
        balanceDaytrade = this.balanceDaytrade,
        balance = this.balance,
        stockAsset = this.asset.map { it.getDto() }
)

private fun IWallet.getSmallDto() = WalletSmallDto(
        name = (this as Wallet).name,
        broker = this.broker
)

private fun IAsset.getDto() = AssetDto(
        stockSymbol = (this as Asset).stock.symbol,
        averageCost = this.averageCost,
        amount = this.amount
)