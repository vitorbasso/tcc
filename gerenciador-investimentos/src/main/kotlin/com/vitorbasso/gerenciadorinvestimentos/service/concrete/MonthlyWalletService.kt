package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MonthlyWalletService(
    private val monthlyWalletRepository: IMonthlyWalletRepository
) {

    fun getMonthlyWallets(clientId: Long) = this.monthlyWalletRepository.findAllByClientId(clientId)

    fun getMonthlyWallet(monthlyWalletId: Long) = this.monthlyWalletRepository.findByIdOrNull(monthlyWalletId)
        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

}