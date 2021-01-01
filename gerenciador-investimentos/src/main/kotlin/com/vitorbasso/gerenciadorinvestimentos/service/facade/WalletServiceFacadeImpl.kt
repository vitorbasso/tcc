package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
internal class WalletServiceFacadeImpl(
    private val walletService: WalletService,
    private val clientService: ClientService
) : IWalletService {

    override fun getWalletCollection() = this.clientService.getClient(SecurityContextUtil.getClientDetails().id).wallet

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

    fun updateBalance(newTransaction: Transaction, sameMonthTransactions: List<Transaction>) {
        this.walletService.processTransaction(newTransaction.asset.wallet.validate(), newTransaction)
    }

    private fun isValid(wallet: Wallet) = wallet.walletMonth.monthValue == LocalDate.now().monthValue

    private fun Wallet.validate() = this.takeIf { isValid(it) } ?: walletService.enforceWalletMonth(this)

}