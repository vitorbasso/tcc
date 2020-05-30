package com.basso.gerenciadorinvestimentos.application.service.concrete

import com.basso.gerenciadorinvestimentos.application.dto.request.WalletUpdateRequest
import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode
import com.basso.gerenciadorinvestimentos.application.exception.CustomBadRequestException
import com.basso.gerenciadorinvestimentos.application.exception.CustomEntityNotFoundException
import com.basso.gerenciadorinvestimentos.application.exception.CustomManagerException
import com.basso.gerenciadorinvestimentos.domain.concrete.Client
import com.basso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.basso.gerenciadorinvestimentos.repository.WalletRepository
import org.springframework.stereotype.Service

@Service
internal class WalletService(
        private val walletRepository: WalletRepository
) {

    fun getWallet(
            cpf: String,
            broker: String,
            exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    )
            = if(exists(cpf, broker)) this.walletRepository.findByBrokerAndClient_Cpf(broker, cpf)
            else throw exception

    fun saveWallet(client: Client, walletToSave: Wallet): Wallet {
        if(exists(client.cpf, walletToSave.broker)) throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

        val walletToSaveComplete = Wallet(
                name = walletToSave.name,
                broker = walletToSave.broker,
                client = client
        )

        return this.walletRepository.save(walletToSaveComplete)
    }

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest)
            = this.walletRepository.save(Wallet(
            id = walletToUpdate.id,
            name = walletUpdateRequest.name ?: walletToUpdate.name,
            broker = walletUpdateRequest.broker ?: walletToUpdate.broker,
            lossDaytrade = walletToUpdate.lossDaytrade,
            loss = walletToUpdate.loss,
            balanceDaytrade = walletToUpdate.balanceDaytrade,
            balance = walletToUpdate.balance,
            client = walletToUpdate.client,
            stockAsset = walletToUpdate.stockAsset
    ))

    fun deleteWallet(wallet: Wallet) {
        this.walletRepository.delete(wallet)
    }

    private fun exists(cpf: String, broker: String)
            = this.walletRepository.existsByBrokerAndClient_Cpf(broker, cpf)

}