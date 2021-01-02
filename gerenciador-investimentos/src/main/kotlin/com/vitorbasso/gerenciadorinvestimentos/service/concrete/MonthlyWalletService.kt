package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
internal class MonthlyWalletService(
    private val monthlyWalletRepository: IMonthlyWalletRepository
) {

    fun getMonthlyWallets(clientId: Long) = this.monthlyWalletRepository.findAllByClientId(clientId)

    fun getMonthlyWallet(
        monthlyWalletId: Long,
        clientId: Long,
        exception: CustomManagerException = CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
    ) = this.monthlyWalletRepository.findByIdOrNull(monthlyWalletId)?.takeIf { it.client.id == clientId }
        ?: throw exception

    fun deleteMonthlyWallet(monthlyWallet: MonthlyWallet) = this.monthlyWalletRepository.delete(monthlyWallet)

}