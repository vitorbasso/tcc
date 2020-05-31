package com.basso.gerenciadorinvestimentos.application.service.proxy

import com.basso.gerenciadorinvestimentos.application.dto.request.ClientRequest
import com.basso.gerenciadorinvestimentos.application.dto.request.ClientUpdateRequest
import com.basso.gerenciadorinvestimentos.application.dto.request.WalletRequest
import com.basso.gerenciadorinvestimentos.application.dto.request.WalletUpdateRequest
import com.basso.gerenciadorinvestimentos.application.dto.response.ClientDto
import com.basso.gerenciadorinvestimentos.application.dto.response.AssetDto
import com.basso.gerenciadorinvestimentos.application.dto.response.WalletDto
import com.basso.gerenciadorinvestimentos.application.dto.response.WalletSmallDto
import com.basso.gerenciadorinvestimentos.application.service.IClientService
import com.basso.gerenciadorinvestimentos.domain.IClient
import com.basso.gerenciadorinvestimentos.domain.IAsset
import com.basso.gerenciadorinvestimentos.domain.IWallet
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import com.basso.gerenciadorinvestimentos.domain.concrete.Asset
import com.basso.gerenciadorinvestimentos.domain.concrete.User
import com.basso.gerenciadorinvestimentos.domain.concrete.Wallet
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class ClientServiceProxyImpl (
        @Qualifier("clientServiceFacadeImpl")
        private val clientService: IClientService
) : IClientService {

    override fun getClient(cpf: String)
            = this.clientService.getClient(cpf).getDto()

    override fun saveClient(clientToSave: IClient)
            = this.clientService.saveClient(clientToSave.getEntity()).getDto()

    override fun updateClient(cpf: String, clientUpdateRequest: ClientUpdateRequest)
            = this.clientService.updateClient(cpf, clientUpdateRequest).getDto()

    override fun deleteClient(cpf: String) {
        this.clientService.deleteClient(cpf)
    }

    override fun getWalletCollection(cpf: String)
            = this.clientService.getWalletCollection(cpf).map { it.getSmallDto() }

    override fun getWallet(cpf: String, broker: String)
            = this.clientService.getWallet(cpf, broker).getDto()

    override fun saveWallet(cpf: String, walletToSave: IWallet)
            = this.clientService.saveWallet(cpf, walletToSave.getEntity()).getDto()

    override fun updateWallet(cpf: String, broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.clientService.updateWallet(
            cpf = cpf,
            broker = broker,
            walletUpdateRequest = walletUpdateRequest
    ).getDto()

    override fun deleteWallet(cpf: String, broker: String) {
        this.clientService.deleteWallet(cpf, broker)
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
        email = (this as ClientRequest).email,
        cpf = this.cpf,
        firstName = this.firstName,
        lastName = this.lastName,
        avatarImage = this.avatarImage,
        user = User(password = this.password)
)

private fun IWallet.getEntity() = Wallet(
        name = (this as WalletRequest).name,
        broker = this.broker,
        client = Client()
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
        name = (this as WalletRequest).name,
        broker = this.broker
)

private fun IAsset.getDto() = AssetDto(
        stockSymbol = (this as Asset).stock.symbol,
        averageCost = this.averageCost,
        amount = this.amount
)