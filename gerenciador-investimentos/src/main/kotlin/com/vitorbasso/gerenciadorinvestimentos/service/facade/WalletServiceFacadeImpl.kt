package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.AccountantUtil
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class WalletServiceFacadeImpl(
    private val walletService: WalletService,
    private val clientService: ClientService
) : IWalletService {

    override fun getWalletCollection()
    = this.clientService.getClient(SecurityContextUtil.getClientDetails().id).wallet

    override fun getWallet(walletId: Long)
    = this.walletService.getWallet(walletId, SecurityContextUtil.getClientDetails().id).validate()

    override fun saveWallet(walletToSave: IWallet) = this.walletService.saveWallet(
        SecurityContextUtil.getClientDetails(),
        walletToSave as Wallet
    )

    override fun updateWallet(walletId: Long, walletUpdateRequest: WalletUpdateRequest)
    = this.walletService.updateWallet(
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

    fun processWalletReport(
        wallet: Wallet,
        walletReport: Map<LocalDate, AccountantUtil.WalletReport>,
        monthlyWalletService: MonthlyWalletServiceFacadeImpl
    ) = wallet.validate().let { walletValidated ->
        walletReport.forEach {
            this.walletService.processWalletReport(
                walletValidated,
                it.value,
                it.key,
                monthlyWalletService
            )
        }
    }

//    fun updateBalance(
//        newTransaction: Transaction,
//        monthlyWalletService: MonthlyWalletServiceFacadeImpl
//    ) = this.walletService.processTransaction(
//        wallet = newTransaction.asset.wallet.validate(),
//        transaction = newTransaction,
//        monthlyWalletService = monthlyWalletService
//    )

    private fun isValid(wallet: Wallet)
    = wallet.walletMonth.withDayOfMonth(1) == LocalDate.now().withDayOfMonth(1)

    private fun Wallet.validate() = this.takeIf { isValid(it) } ?: walletService.enforceWalletMonth(this)

}