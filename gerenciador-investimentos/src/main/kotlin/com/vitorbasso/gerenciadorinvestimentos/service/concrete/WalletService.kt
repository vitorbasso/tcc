package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
internal class WalletService(
    private val walletRepository: IWalletRepository
) {

    fun getWallet(
        client: Client,
        broker: String,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = this.walletRepository.findByBrokerAndClient(broker, client) ?: throw exception

    fun saveWallet(client: Client, walletToSave: Wallet) = if (!exists(client, walletToSave)) this.walletRepository.save(walletToSave.copy(client = client))
    else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest) = this.walletRepository.save(walletToUpdate.copy(
        name = walletUpdateRequest.name ?: walletToUpdate.name,
        broker = walletUpdateRequest.broker ?: walletToUpdate.broker
    ))

    fun deleteWallet(wallet: Wallet) = this.walletRepository.delete(wallet)

    fun enforceWalletMonth(wallet: Wallet) = wallet.copy(
        monthlyBalanceDaytrade = BigDecimal.ZERO,
        monthlyBalance = BigDecimal.ZERO,
        walletMonth = LocalDate.now().withDayOfMonth(1)
    ).let { this.walletRepository.save(it) }

    fun processTransaction(wallet: Wallet, transaction: Transaction) = transaction.value.divide(BigDecimal(transaction.quantity), 20, RoundingMode.HALF_EVEN).let {
        val daytradeValue = it.multiply(BigDecimal(transaction.daytradeQuantity))
        val normalValue = it.multiply(
            BigDecimal(transaction.quantity).subtract(BigDecimal(transaction.daytradeQuantity)).abs()
        )
        when (transaction.type) {
            TransactionType.BUY -> processBuyTransaction(
                wallet = wallet,
                daytradeValue = daytradeValue,
                normalValue = normalValue
            )
            TransactionType.SELL -> processSellTransaction(
                wallet = wallet,
                daytradeValue = daytradeValue,
                normalValue = normalValue
            )
        }
    }.let { this.walletRepository.save(it) }

    private fun processBuyTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        monthlyBalanceDaytrade = wallet.monthlyBalanceDaytrade.subtract(daytradeValue),
        lifetimeBalanceDaytrade = wallet.lifetimeBalanceDaytrade.subtract(daytradeValue),
        monthlyBalance = wallet.monthlyBalance.subtract(normalValue),
        lifetimeBalance = wallet.monthlyBalance.subtract(normalValue)
    )

    private fun processSellTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        monthlyBalanceDaytrade = wallet.monthlyBalanceDaytrade.add(daytradeValue),
        lifetimeBalanceDaytrade = wallet.lifetimeBalanceDaytrade.add(daytradeValue),
        monthlyBalance = wallet.monthlyBalance.add(normalValue),
        lifetimeBalance = wallet.monthlyBalance.add(normalValue)
    )


    private fun exists(client: Client, wallet: Wallet) = this.walletRepository.existsByBrokerAndClient(wallet.broker, client)

}