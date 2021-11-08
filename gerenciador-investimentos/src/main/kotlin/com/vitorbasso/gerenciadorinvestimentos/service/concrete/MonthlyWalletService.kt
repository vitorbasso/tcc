package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

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

    fun getMonthlyWalletByMonth(
        month: LocalDate,
        clientId: Long
    ) = this.monthlyWalletRepository.findByWalletMonthAndClientId(month.atStartOfMonth(), clientId)?.let {
        if (it.client.id == clientId) it
        else throw CustomBadRequestException(ManagerErrorCode.MANAGER_05)
    }

    fun deleteMonthlyWallet(monthlyWallet: MonthlyWallet) = this.monthlyWalletRepository.delete(monthlyWallet)

}