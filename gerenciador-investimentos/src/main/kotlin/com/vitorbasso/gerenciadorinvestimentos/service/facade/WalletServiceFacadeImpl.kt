package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
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

    override fun getWalletCollection(): List<IWallet> {
        val wallets =
            this.clientService.getClient(SecurityContextUtil.getClientDetails().id).wallets.map { it.validate() }
        return wallets.ifEmpty {
            listOf(saveWallet(Wallet(name = DEFAULT_WALLET_NAME)))
        }
    }

    override fun getWallet(walletId: Long) =
        this.walletService.getWallet(walletId, SecurityContextUtil.getClientDetails().id).validate()

    override fun saveWallet(walletToSave: IWallet) = this.walletService.saveWallet(
        SecurityContextUtil.getClientDetails(),
        walletToSave as Wallet
    )

    override fun updateWallet(walletId: Long, walletUpdateRequest: WalletUpdateRequest) =
        this.walletService.updateWallet(
            this.walletService.getWallet(
                walletId = walletId,
                clientId = SecurityContextUtil.getClientDetails().id,
                exception = CustomBadRequestException(ManagerErrorCode.MANAGER_06)
            ).validate(),
            walletUpdateRequest
        )

    override fun deleteWallet(walletId: Long) {
        this.walletService.deleteWallet(
            this.walletService.getWallet(
                walletId = walletId,
                clientId = SecurityContextUtil.getClientDetails().id,
                exception = CustomBadRequestException(ManagerErrorCode.MANAGER_07)
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

    companion object {
        private const val DEFAULT_WALLET_NAME = "DEFAULT"
    }

}
