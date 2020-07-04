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
            asset = walletToUpdate.asset
    ))

    fun deleteWallet(wallet: Wallet) {
        this.walletRepository.delete(wallet)
    }

    private fun exists(cpf: String, broker: String)
            = this.walletRepository.existsByBrokerAndClient_Cpf(broker, cpf)

}