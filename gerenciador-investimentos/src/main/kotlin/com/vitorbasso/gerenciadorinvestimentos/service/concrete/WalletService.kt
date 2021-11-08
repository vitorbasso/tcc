package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
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
        clientId: Long,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = this.walletRepository.findByClientId(clientId) ?: throw exception

    fun deleteWallet(wallet: Wallet) = this.walletRepository.delete(wallet)

    @Transactional
    fun enforceWalletMonth(wallet: Wallet) =
        if (!this.monthlyWalletRepository.existsByWalletIdAndWalletMonth(wallet.id, wallet.walletMonth)) {
            this.monthlyWalletRepository.save(wallet.toMonthlyWallet())
            wallet.copy(
                balanceDaytrade = BigDecimal.ZERO,
                balance = BigDecimal.ZERO,
                walletMonth = LocalDate.now().atStartOfMonth()
            ).let {
                this.walletRepository.save(it)
            }
        } else throw CustomBadRequestException(ManagerErrorCode.MANAGER_04)

    fun processWalletReport(
        wallet: Wallet,
        walletReport: AccountingService.WalletReport,
        reportMonth: LocalDate,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) {
        if (reportMonth.isEqual(wallet.walletMonth)) {
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
                walletMonth = reportMonth
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
    ) = monthlyWalletService.getMonthlyWalletByMonth(walletMonth.atStartOfMonth())
        ?: wallet.toMonthlyWallet()
}

private fun Wallet.toMonthlyWallet() = MonthlyWallet(
    balanceDaytrade = this.balanceDaytrade,
    walletMonth = this.walletMonth.atStartOfMonth(),
    client = this.client,
    walletId = this.id
)