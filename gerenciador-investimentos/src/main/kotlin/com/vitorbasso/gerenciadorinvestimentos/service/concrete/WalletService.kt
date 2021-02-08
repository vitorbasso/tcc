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
            balanceDaytrade = BigDecimal.ZERO,
            balance = BigDecimal.ZERO,
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
                    balance = wallet.balance.add(walletReport.balanceContribution),
                    balanceDaytrade = wallet.balanceDaytrade.add(walletReport.daytradeBalanceContribution),
                    withdrawn = wallet.withdrawn.add(walletReport.withdrawnContribution),
                    withdrawnDaytrade = wallet.withdrawnDaytrade.add(walletReport.daytradeWithdrawnContribution)
                )
            )
        } else {
            this.monthlyWalletRepository.save(getTheMonthlyWallet(
                wallet = wallet,
                monthlyWalletService = monthlyWalletService,
                walletMonth = walletMonth
            ).let {
                it.copy(
                    balance = it.balance.add(walletReport.balanceContribution),
                    balanceDaytrade = it.balanceDaytrade.add(walletReport.daytradeBalanceContribution),
                    withdrawn = it.withdrawn.add(walletReport.withdrawnContribution),
                    withdrawnDaytrade = it.withdrawnDaytrade.add(walletReport.daytradeWithdrawnContribution)
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
            balanceDaytrade = BigDecimal.ZERO,
            balance = BigDecimal.ZERO,
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
    balanceDaytrade = this.balanceDaytrade,
    balance = this.balance,
    walletId = this.id,
    walletMonth = this.walletMonth,
    client = this.client
)