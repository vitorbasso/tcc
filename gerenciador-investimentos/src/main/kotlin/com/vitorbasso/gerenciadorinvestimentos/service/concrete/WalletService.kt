package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.AccountantUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
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

    fun saveWallet(client: Client, walletToSave: Wallet) = if (!exists(client, walletToSave))
        this.walletRepository.save(
            walletToSave.copy(walletMonth = walletToSave.walletMonth.withDayOfMonth(1), client = client)
        )
    else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun updateWallet(walletToUpdate: Wallet, walletUpdateRequest: WalletUpdateRequest)
    = this.walletRepository.save(walletToUpdate.copy(
        name = walletUpdateRequest.name ?: walletToUpdate.name,
        broker = walletUpdateRequest.broker ?: walletToUpdate.broker
    ))

    fun deleteWallet(wallet: Wallet) = this.walletRepository.delete(wallet)

    @Transactional
    fun enforceWalletMonth(wallet: Wallet)
    = if (!this.monthlyWalletRepository.existsByWalletIdAndWalletMonth(wallet.id, wallet.walletMonth)) {
        this.monthlyWalletRepository.save(wallet.toMonthlyWallet())
        wallet.copy(
            monthlyBalanceDaytrade = BigDecimal.ZERO,
            monthlyBalance = BigDecimal.ZERO,
            walletMonth = LocalDate.now().withDayOfMonth(1)
        ).let {
            this.walletRepository.save(it)
        }
    } else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun processWalletReport(
        wallet: Wallet,
        walletReport: AccountantUtil.WalletReport,
        walletMonth: LocalDate,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) {
        if (walletMonth.isEqual(wallet.walletMonth)) {
            this.walletRepository.save(
                wallet.copy(
                    monthlyBalance = wallet.monthlyBalance.add(walletReport.newNormalValue),
                    monthlyBalanceDaytrade = wallet.monthlyBalanceDaytrade.add(walletReport.newDaytradeValue),
                    withdrawn = wallet.withdrawn.add(walletReport.newWithdrawn),
                    withdrawnDaytrade = wallet.withdrawnDaytrade.add(walletReport.newDaytradeWithdrawn)
                )
            )
        } else {
            this.monthlyWalletRepository.save(getTheMonthlyWallet(
                wallet = wallet,
                monthlyWalletService = monthlyWalletService,
                walletMonth = walletMonth
            ).let {
                it.copy(
                    monthlyBalance = it.monthlyBalance.add(walletReport.newNormalValue),
                    monthlyBalanceDaytrade = it.monthlyBalanceDaytrade.add(walletReport.newDaytradeValue),
                    withdrawn = it.withdrawn.add(walletReport.newWithdrawn),
                    withdrawnDaytrade = it.withdrawnDaytrade.add(walletReport.newDaytradeWithdrawn)
                )
            }
            )
        }
    }

    private fun getTheMonthlyWallet(
        wallet: Wallet,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl,
        walletMonth: LocalDate
    ) = monthlyWalletService.getMonthlyWalletByMonth(walletMonth.withDayOfMonth(1))
        ?: MonthlyWallet(
            name = wallet.name,
            broker = wallet.broker,
            monthlyBalanceDaytrade = BigDecimal.ZERO,
            monthlyBalance = BigDecimal.ZERO,
            walletMonth = walletMonth.withDayOfMonth(1),
            walletId = wallet.id,
            client = wallet.client
        )

    private fun exists(client: Client, wallet: Wallet)
    = this.walletRepository.existsByBrokerAndClient(wallet.broker, client)

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