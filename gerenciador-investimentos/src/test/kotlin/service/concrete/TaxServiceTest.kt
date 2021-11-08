package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.core.spec.style.StringSpec
import utils.EasyRandomWrapper.random
import utils.shouldBeEqual
import java.math.BigDecimal
import java.time.LocalDate

class TaxServiceTest : StringSpec() {

    private val taxService = TaxService()

    init {
        "should not be taxed" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("19999")
            val taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            val result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual BigDecimal.ZERO
            result.daytradeWithdrawn shouldBeEqual BigDecimal.ZERO
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should be normal taxed" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("20000")
            val taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            val result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual BigDecimal.ZERO
            result.daytradeWithdrawn shouldBeEqual BigDecimal.ZERO
            result.normalTax shouldBeEqual BigDecimal("2699") // 15% de 18000 ( = 2700) - IRRF de 0,005% de 20000 ( = 1)
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should be daytrade taxed" {
            val balance = BigDecimal("1800")
            val withdrawn = BigDecimal("5000")
            val taxables = listOf(
                wallet(
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn,
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            val result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual BigDecimal.ZERO
            result.withdrawn shouldBeEqual BigDecimal.ZERO
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal("342") // 20% de 1800 ( = 360) - IRRF de 1% de 1800 (= 18)
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should be both taxed" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("20000")
            val taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            val result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal("2699") // 15% de 18000 ( = 2700) - IRRF de 0,005% de 20000 ( = 1)
            result.daytradeTax shouldBeEqual BigDecimal("3420")
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should have normal correct available to deduct" {
            val balance = BigDecimal("-18000")
            val withdrawn = BigDecimal("20000")
            var taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual BigDecimal.ZERO
            result.daytradeWithdrawn shouldBeEqual BigDecimal.ZERO
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual -balance
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO

            val pastBalance = BigDecimal("-12000")
            taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                ),
                walletMonth(
                    balance = pastBalance,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual BigDecimal.ZERO
            result.daytradeWithdrawn shouldBeEqual BigDecimal.ZERO
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual -balance.plus(pastBalance)
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should have daytrade correct available to deduct" {
            val balance = BigDecimal("-18000")
            val withdrawn = BigDecimal("20000")
            var taxables = listOf(
                wallet(
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual BigDecimal.ZERO
            result.withdrawn shouldBeEqual BigDecimal.ZERO
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual -balance
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO

            val pastBalance = BigDecimal("-12000")
            taxables = listOf(
                wallet(
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = BigDecimal("18000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = pastBalance
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual BigDecimal.ZERO
            result.withdrawn shouldBeEqual BigDecimal.ZERO
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual -balance.plus(pastBalance)
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should have both correct available to deduct" {
            val balance = BigDecimal("-18000")
            val withdrawn = BigDecimal("20000")
            var taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = BigDecimal("25000"),
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = BigDecimal("123423")
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual -balance
            result.daytradeAvailableToDeduct shouldBeEqual -balance
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO

            val pastBalance = BigDecimal("-12000")
            taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = pastBalance,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("23423"),
                    balanceDaytrade = pastBalance
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual -balance.plus(pastBalance)
            result.daytradeAvailableToDeduct shouldBeEqual -balance.plus(pastBalance)
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should be normal deducted from tax" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("20000")
            var deducted = BigDecimal("9000")
            var taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = -deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = deducted
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal("1349") // 9000 deduzidos de 18000 = 9000 , 15% de 9000 ( = 1350) - IRRF de 0,005% de 20000 ( = 1)
            result.daytradeTax shouldBeEqual BigDecimal("3420")
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO // nao teve lucro em daytrade para deduzir
            result.deducted shouldBeEqual deducted // pode deduzir todo o valor
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO

            deducted = BigDecimal("20000")
            taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = -deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = deducted
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO // 18000 deduzidos de 18000 = 0
            result.daytradeTax shouldBeEqual BigDecimal("3420")
            result.availableToDeduct shouldBeEqual BigDecimal("2000") // sobrou pra ser deduzido ainda
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO // nao teve lucro em daytrade para deduzir
            result.deducted shouldBeEqual balance // pode deduzir todo o valor
            result.daytradeDeducted shouldBeEqual BigDecimal.ZERO
        }

        "should be daytrade deducted from tax" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("20000")
            var deducted = BigDecimal("9000")
            var taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = -deducted
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal("2699") // nao foi deduzido
            result.daytradeTax shouldBeEqual BigDecimal("1620") // 9000 deduzido de 18000, 20% de 9000 = (1800) - IRRF de 1% de 18000 (180)
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO // nao teve lucro em daytrade para deduzir
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual deducted

            deducted = BigDecimal("20000")
            taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = -deducted
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal("2699") // nao foi deduzido
            result.daytradeTax shouldBeEqual BigDecimal.ZERO // completamente deduzido
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal("2000")// sobrou
            result.deducted shouldBeEqual BigDecimal.ZERO
            result.daytradeDeducted shouldBeEqual balance
        }

        "should be both deducted from tax correctly" {
            val balance = BigDecimal("18000")
            val withdrawn = BigDecimal("20000")
            var deducted = BigDecimal("9000")
            var taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = -deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = -deducted
                )
            )
            var result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal("1349")
            result.daytradeTax shouldBeEqual BigDecimal("1620")
            result.availableToDeduct shouldBeEqual BigDecimal.ZERO
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal.ZERO
            result.deducted shouldBeEqual deducted // pode deduzir todo o valor
            result.daytradeDeducted shouldBeEqual deducted

            deducted = BigDecimal("20000")
            taxables = listOf(
                wallet(
                    balance = balance,
                    withdrawn = withdrawn,
                    balanceDaytrade = balance,
                    withdrawnDaytrade = withdrawn
                ),
                walletMonth(
                    balance = -deducted,
                    withdrawn = BigDecimal("21000"),
                    withdrawnDaytrade = BigDecimal("21000"),
                    balanceDaytrade = -deducted
                )
            )
            result = taxService.calculateTax(taxables)
            result.balance shouldBeEqual balance
            result.withdrawn shouldBeEqual withdrawn
            result.daytradeBalance shouldBeEqual balance
            result.daytradeWithdrawn shouldBeEqual withdrawn
            result.normalTax shouldBeEqual BigDecimal.ZERO
            result.daytradeTax shouldBeEqual BigDecimal.ZERO
            result.availableToDeduct shouldBeEqual BigDecimal("2000")
            result.daytradeAvailableToDeduct shouldBeEqual BigDecimal("2000")
            result.deducted shouldBeEqual balance
            result.daytradeDeducted shouldBeEqual balance
        }
    }

    private fun wallet(
        balance: BigDecimal = BigDecimal.ZERO,
        balanceDaytrade: BigDecimal = BigDecimal.ZERO,
        withdrawn: BigDecimal = BigDecimal.ZERO,
        withdrawnDaytrade: BigDecimal = BigDecimal.ZERO,
        walletMonth: LocalDate = LocalDate.now()
    ) = random<Wallet>().copy(
        balance = balance,
        balanceDaytrade = balanceDaytrade,
        withdrawn = withdrawn,
        withdrawnDaytrade = withdrawnDaytrade,
        walletMonth = walletMonth.atStartOfMonth()
    )

    private fun walletMonth(
        balance: BigDecimal = BigDecimal.ZERO,
        balanceDaytrade: BigDecimal = BigDecimal.ZERO,
        withdrawn: BigDecimal = BigDecimal.ZERO,
        withdrawnDaytrade: BigDecimal = BigDecimal.ZERO,
        walletMonth: LocalDate = LocalDate.now().minusMonths(1)
    ) = random<MonthlyWallet>().copy(
        balance = balance,
        balanceDaytrade = balanceDaytrade,
        withdrawn = withdrawn,
        withdrawnDaytrade = withdrawnDaytrade,
        walletMonth = walletMonth.atStartOfMonth()
    )

}