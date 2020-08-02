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

    override fun getClient() = this.clientService.getClient().getDto()

    override fun saveClient(clientToSave: IClient)
            = this.clientService.saveClient(clientToSave.getEntity()).getDto()

    override fun updateClient(clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(clientUpdateRequest).getDto()

    override fun deleteClient() {
        this.clientService.deleteClient()
    }

    override fun getWalletCollection()
            = this.clientService.getWalletCollection().map { it.getSmallDto() }

    override fun getWallet(broker: String)
            = this.clientService.getWallet(broker).getDto()

    override fun saveWallet(walletToSave: IWallet)
            = this.clientService.saveWallet(walletToSave.getEntity()).getDto()

    override fun updateWallet(broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.clientService.updateWallet(
            broker = broker,
            walletUpdateRequest = walletUpdateRequest
    ).getDto()

    override fun deleteWallet(broker: String) {
        this.clientService.deleteWallet(broker)
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
        stockSymbol = (this as Asset).stock.ticker,
        averageCost = this.averageCost,
        amount = this.amount
)