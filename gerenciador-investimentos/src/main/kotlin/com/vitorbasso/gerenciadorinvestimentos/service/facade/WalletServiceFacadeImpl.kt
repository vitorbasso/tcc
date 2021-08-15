package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IAccountingServiceSubscriber
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class WalletServiceFacadeImpl(
    private val walletService: WalletService,
    private val monthlyWalletService: MonthlyWalletServiceFacadeImpl,
    private val clientService: ClientService
) : IWalletService, IAccountingServiceSubscriber {

    override fun getWallet() =
        this.walletService.getWallet(SecurityContextUtil.getClientDetails().id).validate()

    override fun deleteWallet(walletId: Long) {
        this.walletService.deleteWallet(
            this.walletService.getWallet(
                clientId = SecurityContextUtil.getClientDetails().id,
                exception = CustomBadRequestException(ManagerErrorCode.MANAGER_06)
            ).validate()
        )
    }

    override fun processAccountantReport(
        transaction: Transaction,
        accountantReport: AccountingService.AccountantReport
    ): AccountingService.AccountantReport {
        transaction.asset.wallet.validate().let { walletValidated ->
            accountantReport.walletsReport.forEach {
                this.walletService.processWalletReport(
                    wallet = walletValidated,
                    walletReport = it.value,
                    reportMonth = it.key,
                    monthlyWalletService = monthlyWalletService
                )
            }
        }
        return accountantReport
    }

    private fun Wallet.validate() = this.takeIf { isValid(it) } ?: walletService.enforceWalletMonth(this)

    private fun isValid(wallet: Wallet) = wallet.walletMonth.atStartOfMonth() == LocalDate.now().atStartOfMonth()

}
