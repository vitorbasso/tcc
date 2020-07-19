package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.WalletRepository
import org.springframework.stereotype.Service

@Service
internal class WalletService(
        private val walletRepository: WalletRepository
) {

    fun getWallet(
            id: Long,
            broker: String,
            exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    )
            = this.walletRepository.findByBrokerAndClientId(broker, id) ?: throw exception

    fun saveWallet(client: Client, walletToSave: Wallet): Wallet {
        if(exists(client.id, walletToSave.broker)) throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

        val walletToSaveComplete = Wallet(
                name = walletToSave.name,
                broker = walletToSave.broker,
                client = client
        )

        return this.walletRepository.save(walletToSaveComplete)
    }

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest)
            = this.walletRepository.save(walletToUpdate.copy(
            name = walletUpdateRequest.name ?: walletToUpdate.name,
            broker = walletUpdateRequest.broker ?: walletToUpdate.broker
    ))

    fun deleteWallet(wallet: Wallet) {
        this.walletRepository.delete(wallet)
    }

    private fun exists(id: Long, broker: String)
            = this.walletRepository.existsByBrokerAndClientId(broker, id)

}