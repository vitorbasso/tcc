package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
internal class WalletService(
    private val walletRepository: IWalletRepository,
    private val monthlyWalletRepository: IMonthlyWalletRepository
) {

    fun getWallet(
        walletId: Long,
        clientId: Long,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = this.walletRepository.findByIdOrNull(walletId)?.takeIf { it.client.id == clientId } ?: throw exception

    fun saveWallet(client: Client, walletToSave: Wallet) = if (!exists(client, walletToSave)) this.walletRepository.save(walletToSave.copy(client = client))
    else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest) = this.walletRepository.save(walletToUpdate.copy(
        name = walletUpdateRequest.name ?: walletToUpdate.name,
        broker = walletUpdateRequest.broker ?: walletToUpdate.broker
    ))

    fun deleteWallet(wallet: Wallet) = this.walletRepository.delete(wallet)

    @Transactional
    fun enforceWalletMonth(wallet: Wallet) = if (!this.monthlyWalletRepository.existsByWalletIdAndWalletMonth(wallet.id, wallet.walletMonth)) {
        this.monthlyWalletRepository.save(wallet.toMonthlyWallet())
        wallet.copy(
            monthlyBalanceDaytrade = BigDecimal.ZERO,
            monthlyBalance = BigDecimal.ZERO,
            walletMonth = LocalDate.now().withDayOfMonth(1)
        ).let {
            this.walletRepository.save(it)
        }
    } else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun processTransaction(
        wallet: Wallet,
        transaction: Transaction,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = transaction.value.divide(BigDecimal(transaction.quantity), 20, RoundingMode.HALF_EVEN).let {
        val daytradeValue = it.multiply(BigDecimal(transaction.daytradeQuantity))
        val normalValue = it.multiply(
            BigDecimal(transaction.quantity).subtract(BigDecimal(transaction.daytradeQuantity)).abs()
        )
        val monthlyWallet = monthlyWalletService.getMonthlyWalletByMonth(transaction.transactionDate) ?: MonthlyWallet(
            name = wallet.name,
            broker = wallet.broker,
            monthlyBalanceDaytrade = BigDecimal.ZERO,
            monthlyBalance = BigDecimal.ZERO,
            walletMonth = transaction.transactionDate,
            walletId = wallet.id,
            client = wallet.client
        )

        when (transaction.type) {
            TransactionType.BUY -> processBuyTransaction(
                wallet = wallet,
                monthlyWallet = monthlyWallet,
                transactionDate = transaction.transactionDate,
                daytradeValue = daytradeValue,
                normalValue = normalValue,
                monthlyWalletService
            )
            TransactionType.SELL -> processSellTransaction(
                wallet = wallet,
                monthlyWallet = monthlyWallet,
                transactionDate = transaction.transactionDate,
                daytradeValue = daytradeValue,
                normalValue = normalValue,
                monthlyWalletService
            )
        }
    }.let { this.walletRepository.save(it) }

    private fun processBuyTransaction(
        wallet: Wallet,
        monthlyWallet: MonthlyWallet,
        transactionDate: LocalDate,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = processLifetimeBuyTransaction(
        wallet = wallet,
        daytradeValue = daytradeValue,
        normalValue = normalValue
    ).let {
        if (transactionDate.withDayOfMonth(1) == LocalDate.now().withDayOfMonth(1))
            processMonthlyBuyTransaction(it, daytradeValue, normalValue)
        else {
            proccessMonthlyWalletBuyTransaction(
                monthlyWallet = monthlyWallet,
                daytradeValue = daytradeValue,
                normalValue = normalValue,
                monthlyWalletService = monthlyWalletService
            )
            it
        }
    }

    private fun proccessMonthlyWalletBuyTransaction(
        monthlyWallet: MonthlyWallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = monthlyWalletService.saveMonthlyWallet(monthlyWallet.copy(
        monthlyBalanceDaytrade = monthlyWallet.monthlyBalanceDaytrade.subtract(daytradeValue),
        monthlyBalance = monthlyWallet.monthlyBalance.subtract(normalValue)
    ))

    private fun processSellTransaction(
        wallet: Wallet,
        monthlyWallet: MonthlyWallet,
        transactionDate: LocalDate,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = processLifetimeSellTransaction(
        wallet = wallet,
        daytradeValue = daytradeValue,
        normalValue = normalValue
    ).let {
        if (transactionDate.withDayOfMonth(1) == LocalDate.now().withDayOfMonth(1))
            processMonthlySellTransaction(it, daytradeValue, normalValue)
        else {
            proccessMonthlyWalletSellTransaction(
                monthlyWallet = monthlyWallet,
                daytradeValue = daytradeValue,
                normalValue = normalValue,
                monthlyWalletService = monthlyWalletService
            )
            it
        }
    }

    private fun proccessMonthlyWalletSellTransaction(
        monthlyWallet: MonthlyWallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = monthlyWalletService.saveMonthlyWallet(monthlyWallet.copy(
        monthlyBalanceDaytrade = monthlyWallet.monthlyBalanceDaytrade.add(daytradeValue),
        monthlyBalance = monthlyWallet.monthlyBalance.add(normalValue)
    ))

    private fun processMonthlyBuyTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        monthlyBalanceDaytrade = wallet.monthlyBalanceDaytrade.subtract(daytradeValue),
        monthlyBalance = wallet.monthlyBalance.subtract(normalValue)
    )

    private fun processMonthlySellTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        monthlyBalanceDaytrade = wallet.monthlyBalanceDaytrade.add(daytradeValue),
        monthlyBalance = wallet.monthlyBalance.add(normalValue)
    )

    private fun processLifetimeBuyTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        lifetimeBalanceDaytrade = wallet.lifetimeBalanceDaytrade.subtract(daytradeValue),
        lifetimeBalance = wallet.lifetimeBalance.subtract(normalValue)
    )

    private fun processLifetimeSellTransaction(
        wallet: Wallet,
        daytradeValue: BigDecimal,
        normalValue: BigDecimal
    ) = wallet.copy(
        lifetimeBalanceDaytrade = wallet.lifetimeBalanceDaytrade.add(daytradeValue),
        lifetimeBalance = wallet.lifetimeBalance.add(normalValue)
    )

    private fun exists(client: Client, wallet: Wallet) = this.walletRepository.existsByBrokerAndClient(wallet.broker, client)

}

private fun Wallet.toMonthlyWallet() = MonthlyWallet(
    name = this.name,
    broker = this.broker,
    monthlyBalanceDaytrade = this.monthlyBalanceDaytrade,
    monthlyBalance = this.monthlyBalance,
    walletId = this.id,
    walletMonth = this.walletMonth,
    client = this.client
)