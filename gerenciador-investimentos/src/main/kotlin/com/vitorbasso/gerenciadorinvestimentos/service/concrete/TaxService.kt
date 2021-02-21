package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Tax
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.repository.ITaxRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
internal class TaxService(
    private val taxRepository: ITaxRepository,
    private val walletService: WalletService
) {

    fun getTax(month: LocalDate, client: Client) = this.taxRepository.findByMonthAndClient(month, client)
//        ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)
        ?: calculateTax(this.walletService.getWallet(1, clientId = client.id))

    fun calculateTax(wallet: Wallet): Tax {
        return Tax(
            tax = getTax(wallet),
            daytradeTax = getDaytradeTax(wallet),
            availableToDeduct = getAvailableToDeduct(wallet),
            daytradeAvailableToDeduct = getDaytradeAvailableToDeduct(wallet),
            month = wallet.walletMonth
        )
    }

    private fun getTax(wallet: Wallet) =
        wallet.balance.multiply(PERCENT_15).takeIf { isTaxable(wallet) && isPositive(it) }
            ?.minus(wallet.withdrawn.multiply(IRRF)) ?: BigDecimal.ZERO

    private fun getDaytradeTax(wallet: Wallet) =
        wallet.balanceDaytrade.multiply(PERCENT_20).takeIf { isPositive(it) }
            ?.minus(wallet.withdrawnDaytrade.multiply(DAYTRADE_IRRF)) ?: BigDecimal.ZERO

    private fun getAvailableToDeduct(wallet: Wallet) = wallet.balance.takeIf { !isPositive(it) } ?: BigDecimal.ZERO

    private fun getDaytradeAvailableToDeduct(wallet: Wallet) =
        wallet.balanceDaytrade.takeIf { !isPositive(it) } ?: BigDecimal.ZERO

    private fun isTaxable(wallet: Wallet) = wallet.withdrawn.compareTo(MAX_WITHDRAWN) >= 0

    private fun isPositive(value: BigDecimal) = value.compareTo(BigDecimal.ZERO) >= 0

    companion object {
        private val MAX_WITHDRAWN = BigDecimal("20000")
        private val PERCENT_15 = BigDecimal("0.15")
        private val PERCENT_20 = BigDecimal("0.20")
        private val IRRF = BigDecimal("0.00005")
        private val DAYTRADE_IRRF = BigDecimal("0.01")
    }

}