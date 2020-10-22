package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import org.springframework.stereotype.Service

@Service
internal class WalletService(
    private val walletRepository: IWalletRepository
) {

    fun getWallet(
        client: Client,
        broker: String,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = this.walletRepository.findByBrokerAndClient(broker, client) ?: throw exception

    fun saveWallet(client: Client, walletToSave: Wallet)
        = if (!exists(client, walletToSave)) this.walletRepository.save(walletToSave.copy(client = client))
    else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest)
        = this.walletRepository.save(walletToUpdate.copy(
        name = walletUpdateRequest.name ?: walletToUpdate.name,
        broker = walletUpdateRequest.broker ?: walletToUpdate.broker
    ))

    fun deleteWallet(wallet: Wallet) {
        this.walletRepository.delete(wallet)
    }

    private fun exists(client: Client, wallet: Wallet)
        = this.walletRepository.existsByBrokerAndClient(wallet.broker, client)

}