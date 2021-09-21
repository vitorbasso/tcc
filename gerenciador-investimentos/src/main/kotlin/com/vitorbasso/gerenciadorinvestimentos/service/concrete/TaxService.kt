package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
internal class TaxService {

    fun calculateTax(
        wallets: List<ITaxable>
    ): TaxInfo {
        val walletsByMonth = wallets.groupBy { it.walletMonth }.toSortedMap()
        return walletsByMonth.values.fold(TaxInfo()) { taxInfo, taxables ->
            val taxable = taxables.single()
            val deducted = if (withdrawnOverAllowance(taxable.withdrawn)) {
                calculateDeducted(balance = taxable.balance, availableToDeduct = taxInfo.availableToDeduct)
            } else BigDecimal.ZERO
            val daytradeDeducted =
                calculateDeducted(
                    balance = taxable.balanceDaytrade,
                    availableToDeduct = taxInfo.daytradeAvailableToDeduct
                )
            val availableToDeduct =
                calculateAvailableToDeduct(
                    balance = taxable.balance,
                    availableToDeduct = taxInfo.availableToDeduct,
                    deducted = deducted
                )
            val daytradeAvailableToDeduct = calculateAvailableToDeduct(
                balance = taxable.balanceDaytrade,
                availableToDeduct = taxInfo.daytradeAvailableToDeduct,
                deducted = daytradeDeducted
            )
            val normalTax = if (withdrawnOverAllowance(taxable.withdrawn)) {
                calculateTax(balance = taxable.balance, deducted = deducted, irrfBase = taxable.withdrawn)
            } else BigDecimal.ZERO
            val daytradeTax = calculateTax(
                balance = taxable.balanceDaytrade,
                deducted = daytradeDeducted,
                taxPercent = PERCENT_20,
                irrfBase = taxable.balanceDaytrade,
                irrf = DAYTRADE_IRRF
            )
            TaxInfo(
                taxable = taxable,
                deducted = deducted,
                daytradeDeducted = daytradeDeducted,
                availableToDeduct = availableToDeduct,
                daytradeAvailableToDeduct = daytradeAvailableToDeduct,
                normalTax = normalTax.takeIf { isPositive(it) } ?: BigDecimal.ZERO,
                daytradeTax = daytradeTax.takeIf { isPositive(it) } ?: BigDecimal.ZERO
            )
        }
    }

    private fun calculateTax(
        balance: BigDecimal,
        deducted: BigDecimal,
        taxPercent: BigDecimal = PERCENT_15,
        irrfBase: BigDecimal,
        irrf: BigDecimal = IRRF
    ) = if (isPositive(balance)) balance.minus(deducted).multiply(taxPercent).minus(irrfBase.multiply(irrf))
    else BigDecimal.ZERO

    private fun calculateDeducted(balance: BigDecimal, availableToDeduct: BigDecimal) = if (isPositive(balance)) {
        if (availableToDeduct.compareTo(balance) <= 0) availableToDeduct else balance
    } else BigDecimal.ZERO

    private fun calculateAvailableToDeduct(
        balance: BigDecimal,
        availableToDeduct: BigDecimal,
        deducted: BigDecimal
    ) =
        availableToDeduct.let {
            if (!isPositive(deducted)) it.plus(deducted) else it
        }.let {
            if (!isPositive(balance)) it.minus(balance) else it
        }

    private fun withdrawnOverAllowance(withdrawn: BigDecimal) = withdrawn.compareTo(MAX_WITHDRAWN) >= 0

    private fun isPositive(value: BigDecimal) = value.compareTo(BigDecimal.ZERO) >= 0

    data class TaxInfo(
        val balance: BigDecimal = BigDecimal.ZERO,
        val daytradeBalance: BigDecimal = BigDecimal.ZERO,
        val normalTax: BigDecimal = BigDecimal.ZERO,
        val daytradeTax: BigDecimal = BigDecimal.ZERO,
        val availableToDeduct: BigDecimal = BigDecimal.ZERO,
        val daytradeAvailableToDeduct: BigDecimal = BigDecimal.ZERO,
        val deducted: BigDecimal = BigDecimal.ZERO,
        val daytradeDeducted: BigDecimal = BigDecimal.ZERO,
        val withdrawn: BigDecimal = BigDecimal.ZERO,
        val daytradeWithdrawn: BigDecimal = BigDecimal.ZERO
    ) : ITax {
        constructor(
            taxable: ITaxable,
            deducted: BigDecimal,
            daytradeDeducted: BigDecimal,
            availableToDeduct: BigDecimal,
            daytradeAvailableToDeduct: BigDecimal,
            normalTax: BigDecimal,
            daytradeTax: BigDecimal
        ) : this(
            balance = taxable.balance,
            withdrawn = taxable.withdrawn,
            daytradeBalance = taxable.balanceDaytrade,
            daytradeWithdrawn = taxable.withdrawnDaytrade,
            deducted = deducted,
            daytradeDeducted = daytradeDeducted,
            availableToDeduct = availableToDeduct.minus(deducted),
            daytradeAvailableToDeduct = daytradeAvailableToDeduct.minus(daytradeDeducted),
            normalTax = normalTax,
            daytradeTax = daytradeTax
        )
    }

    companion object {
        private val MAX_WITHDRAWN = BigDecimal("20000")
        private val PERCENT_15 = BigDecimal("0.15")
        private val PERCENT_20 = BigDecimal("0.20")
        private val IRRF = BigDecimal("0.00005")
        private val DAYTRADE_IRRF = BigDecimal("0.01")
    }

}