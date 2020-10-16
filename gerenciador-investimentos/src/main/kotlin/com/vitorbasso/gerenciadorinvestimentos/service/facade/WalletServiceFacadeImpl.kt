package com.vitorbasso.gerenciadorinvestimentos.service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.dto.request.WalletUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import org.springframework.stereotype.Service

@Service
internal class WalletServiceFacadeImpl(
        private val walletService: WalletService,
        private val clientService: ClientService
): IWalletService {

    override fun getWalletCollection()
            = this.clientService.getClient(SecurityContextUtil.getClientDetails().id).wallet

    override fun getWallet(broker: String)
            = this.walletService.getWallet(SecurityContextUtil.getClientDetails(), broker)

    override fun saveWallet(walletToSave: IWallet)
            = this.walletService.saveWallet(
            SecurityContextUtil.getClientDetails(),
            walletToSave as Wallet
    )

    override fun updateWallet(broker: String, walletUpdateRequest: WalletUpdateRequest)
            = this.walletService.updateWallet(
            this.walletService.getWallet(
                    client = SecurityContextUtil.getClientDetails(),
                    broker = broker,
                    exception = CustomBadRequestException(ManagerErrorCode.MANAGER_06)
            ),
            walletUpdateRequest
    )

    override fun deleteWallet(broker: String) {
        this.walletService.deleteWallet(
                this.walletService.getWallet(
                        client = SecurityContextUtil.getClientDetails(),
                        broker = broker,
                        exception = CustomBadRequestException(ManagerErrorCode.MANAGER_07)
                )
        )
    }

}