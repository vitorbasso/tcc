package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.ITax
import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.TaxDeductible
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TaxDeductibleRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.repository.ITaxRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
internal class TaxService(
    private val taxRepository: ITaxRepository
) {

    fun getTaxDeductibles(month: LocalDate, client: Client) =
        this.taxRepository.findAllByClientAndMonthLessThanEqual(client, month)

    fun deductFromTax(
        tax: TaxInfo,
        deductible: TaxDeductible,
        deductibleRequest: TaxDeductibleRequest
    ): TaxInfo {
        if (!validateDeduction(tax, deductibleRequest)) throw CustomBadRequestException(ManagerErrorCode.MANAGER_11)
        val deducted = deductible.deducted.add(deductibleRequest.deducted)
        val daytradeDeducted = deductible.daytradeDeducted.add(deductibleRequest.daytradeDeducted)
        this.taxRepository.save(
            deductible.copy(
                deducted = deducted,
                daytradeDeducted = daytradeDeducted
            )
        )
        return tax.copy(
            normalTax = getTaxDue(
                balance = tax.balance,
                withdrawn = tax.withdrawn,
                tax = PERCENT_15,
                irrf = IRRF,
                deducted = deducted,
                type = OperationType.NORMAL
            ).takeIf { tax.withdrawn.compareTo(MAX_WITHDRAWN) >= 0 } ?: BigDecimal.ZERO,
            daytradeTax = getTaxDue(
                balance = tax.daytradeBalance,
                withdrawn = tax.daytradeWithdrawn,
                tax = PERCENT_20,
                irrf = DAYTRADE_IRRF,
                deducted = daytradeDeducted,
                type = OperationType.DAYTRADE
            ),
            availableToDeduct = tax.availableToDeduct.subtract(deductibleRequest.deducted),
            daytradeAvailableToDeduct = tax.daytradeAvailableToDeduct.subtract(deductibleRequest.daytradeDeducted),
            deducted = deducted,
            daytradeDeducted = daytradeDeducted
        )
    }

    fun calculateTax(
        wallets: List<ITaxable>
    ): TaxInfo {
        val walletsByMonth = wallets.groupBy { it.walletMonth }.toSortedMap()
        return walletsByMonth.values.fold(TaxInfo()) { taxInfo, taxables ->
            val taxable = taxables.single()
            val availableToDeduct = taxInfo.availableToDeduct
            val daytradeAvailableToDeduct = taxInfo.availableToDeduct
            val deducted = if (withdrawnOverAllowance(taxable.withdrawn)) {
                if (isProfit(taxable.balance)) {
                    if (availableToDeduct.compareTo(taxable.balance) <= 0) {
                        availableToDeduct
                    } else {
                        taxable.balance
                    }
                } else BigDecimal.ZERO
            } else BigDecimal.ZERO
            val daytradeDeducted = if (isProfit(taxable.balanceDaytrade)) {
                if (daytradeAvailableToDeduct.compareTo(taxable.balanceDaytrade) <= 0) {
                    daytradeAvailableToDeduct
                } else {
                    taxable.balanceDaytrade
                }
            } else BigDecimal.ZERO
            val normalTax = if (isProfit(taxable.balance)) {
                if (withdrawnOverAllowance(taxable.withdrawn)) {
                    taxable.balance.minus(deducted).multiply(PERCENT_15).minus(taxable.withdrawn.multiply(IRRF))
                } else BigDecimal.ZERO
            } else BigDecimal.ZERO
            val daytradeTax = if (isProfit(taxable.balanceDaytrade)) {
                taxable.balanceDaytrade.minus(daytradeDeducted).multiply(PERCENT_20).minus(
                    taxable.balanceDaytrade.multiply(
                        DAYTRADE_IRRF
                    )
                )
            } else BigDecimal.ZERO
            TaxInfo(
                balance = taxable.balance,
                withdrawn = taxable.withdrawn,
                daytradeBalance = taxable.balanceDaytrade,
                daytradeWithdrawn = taxable.withdrawnDaytrade,
                deducted = deducted,
                daytradeDeducted = daytradeDeducted,
                availableToDeduct = taxInfo.availableToDeduct.minus(deducted),
                daytradeAvailableToDeduct = taxInfo.daytradeAvailableToDeduct.minus(daytradeDeducted),
                normalTax = normalTax,
                daytradeTax = daytradeTax
            )
        }
    }

    private fun isProfit(balance: BigDecimal) = isPositive(balance)

    private fun withdrawnOverAllowance(withdrawn: BigDecimal) = withdrawn.compareTo(MAX_WITHDRAWN) >= 0

    fun calculateTax(
        month: LocalDate,
        wallets: List<ITaxable>,
        deductibles: List<TaxDeductible>
    ): TaxInfo {
        val walletsByMonth = wallets.toMonthMap()

        return walletsByMonth.toList().fold(TaxInfo()) { info, taxables ->
            val deducted = deductibles.find { it.month.equals(taxables.first) } ?: TaxDeductible()
            val taxInfo = getTaxInfo(taxables, deducted)
            taxInfo.copy(
                availableToDeduct = taxInfo.availableToDeduct.add(info.availableToDeduct),
                daytradeAvailableToDeduct = taxInfo.daytradeAvailableToDeduct.add(info.daytradeAvailableToDeduct)
            )
        }
    }

    private fun getTaxInfo(
        wallets: Pair<LocalDate, List<ITaxable>>,
        deducted: TaxDeductible
    ) = wallets.second.fold(TaxInfo()) { taxInfo, wallet ->
        taxInfo.copy(
            balance = taxInfo.balance.add(wallet.balance),
            daytradeBalance = taxInfo.daytradeBalance.add(wallet.balanceDaytrade),
            withdrawn = taxInfo.withdrawn.add(wallet.withdrawn),
            daytradeWithdrawn = taxInfo.daytradeWithdrawn.add(wallet.withdrawnDaytrade),
            availableToDeduct = taxInfo.availableToDeduct.add(getAvailableDeductible(wallet.balance)),
            daytradeAvailableToDeduct = taxInfo.daytradeAvailableToDeduct.add(
                getAvailableDeductible(wallet.balanceDaytrade)
            )
        )
    }.let { tax ->
        tax.copy(
            normalTax = getTaxDue(
                balance = tax.balance,
                withdrawn = tax.withdrawn,
                tax = PERCENT_15,
                irrf = IRRF,
                deducted = deducted.deducted,
                type = OperationType.NORMAL
            ).takeIf { tax.withdrawn.compareTo(MAX_WITHDRAWN) >= 0 } ?: BigDecimal.ZERO,
            daytradeTax = getTaxDue(
                balance = tax.daytradeBalance,
                withdrawn = tax.daytradeWithdrawn,
                tax = PERCENT_20,
                irrf = DAYTRADE_IRRF,
                deducted = deducted.daytradeDeducted,
                type = OperationType.DAYTRADE
            ),
            availableToDeduct = tax.availableToDeduct.subtract(deducted.deducted),
            daytradeAvailableToDeduct = tax.daytradeAvailableToDeduct.subtract(deducted.daytradeDeducted),
            deducted = deducted.deducted,
            daytradeDeducted = deducted.daytradeDeducted
        )
    }

    private fun getTaxDue(
        balance: BigDecimal,
        withdrawn: BigDecimal,
        tax: BigDecimal,
        irrf: BigDecimal,
        deducted: BigDecimal = BigDecimal.ZERO,
        type: OperationType
    ) = (balance.subtract(deducted).multiply(tax)
        .subtract(
            when (type) {
                OperationType.NORMAL -> withdrawn
                OperationType.DAYTRADE -> balance
            }.multiply(irrf)
        )).takeIf { isPositive(it) } ?: BigDecimal.ZERO

    private fun validateDeduction(tax: TaxInfo, deduction: TaxDeductibleRequest) =
        tax.availableToDeduct.compareTo(deduction.deducted) >= 0 &&
            tax.daytradeAvailableToDeduct.compareTo(deduction.daytradeDeducted) >= 0 &&
            (tax.balance.takeIf { isPositive(it) } ?: BigDecimal.ZERO).compareTo(deduction.deducted) >= 0 &&
            tax.daytradeBalance.compareTo(deduction.daytradeDeducted) >= 0

    private fun getAvailableDeductible(balance: BigDecimal) =
        if (!isPositive(balance)) balance.abs() else BigDecimal.ZERO

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
    ) : ITax

    private enum class OperationType {
        DAYTRADE, NORMAL
    }

    companion object {
        private val MAX_WITHDRAWN = BigDecimal("20000")
        private val PERCENT_15 = BigDecimal("0.15")
        private val PERCENT_20 = BigDecimal("0.20")
        private val IRRF = BigDecimal("0.00005")
        private val DAYTRADE_IRRF = BigDecimal("0.01")
    }

}

private fun List<ITaxable>.toMonthMap() =
    mutableMapOf<LocalDate, List<ITaxable>>().let { map ->
        this.map { it.walletMonth to it }.forEach {
            map[it.first] = map[it.first]?.plus(it.second) ?: listOf(it.second)
        }
        map.toSortedMap()
    }