package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.repository.IWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.equality.shouldBeEqualToComparingFieldsExcept
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import utils.EasyRandomWrapper.random
import utils.now
import utils.shouldBeEqual
import java.math.BigDecimal
import java.time.LocalDate

class WalletServiceTest : StringSpec() {

    private val repository = mockk<IWalletRepository>()
    private val monthRepository = mockk<IMonthlyWalletRepository>()
    private val service = WalletService(repository, monthRepository)

    init {
        "should get wallet" {
            val wallet = wallet()
            every { repository.findByClientId(any()) } returns wallet
            shouldNotThrowAny {
                val result = service.getWallet(random())
                result shouldBe wallet
            }
            verify(exactly = 1) { repository.findByClientId(any()) }
        }
        "should throw when wallet not found" {
            every { repository.findByClientId(any()) } returns null
            shouldThrow<CustomEntityNotFoundException> {
                service.getWallet(random())
            }
            verify(exactly = 1) { repository.findByClientId(any()) }
        }

        "should delete wallet" {
            every { repository.delete(any()) } just runs
            service.deleteWallet(random())
            verify(exactly = 1) { repository.delete(any()) }
        }

        "should enforce wallet month" {
            val wallet = wallet()
            val monthlyWallet = wallet.toMonthlyWallet()
            every { monthRepository.existsByWalletIdAndWalletMonth(any(), any()) } returns false
            every { monthRepository.save(any()) } answers { firstArg() }
            every { repository.save(any()) } answers { firstArg() }

            shouldNotThrowAny {
                service.enforceWalletMonth(wallet)
            }

            verify(exactly = 1) { monthRepository.existsByWalletIdAndWalletMonth(any(), any()) }
            val monthSlot = slot<MonthlyWallet>()
            verify(exactly = 1) { monthRepository.save(capture(monthSlot)) }
            monthSlot.captured shouldBe monthlyWallet
            val slot = slot<Wallet>()
            verify(exactly = 1) { repository.save(capture(slot)) }
            slot.captured.should {
                it.shouldBeEqualToComparingFieldsExcept(
                    wallet,
                    Wallet::balanceDaytrade,
                    Wallet::balance,
                    Wallet::walletMonth,
                    Wallet::dateCreated,
                    Wallet::dateUpdated
                )
                it.balanceDaytrade shouldBeEqual BigDecimal.ZERO
                it.balance shouldBeEqual BigDecimal.ZERO
                it.walletMonth shouldBe LocalDate.now().atStartOfMonth()
            }

        }

        "should throw when wallet month already exists " {
            every { monthRepository.existsByWalletIdAndWalletMonth(any(), any()) } returns true
            shouldThrow<CustomBadRequestException> {
                service.enforceWalletMonth(random())
            }
            verify(exactly = 1) { monthRepository.existsByWalletIdAndWalletMonth(any(), any()) }
            verify(exactly = 0) { monthRepository.save(any()) }
            verify(exactly = 0) { repository.save(any()) }
        }

        "should process wallet report when same month" {
            val reportMonth = now().atStartOfMonth()
            val wallet = wallet().copy(walletMonth = reportMonth)
            val walletReport = random<AccountingService.WalletReport>()
            val monthService = mockk<MonthlyWalletServiceFacadeImpl>()
            every { repository.save(any()) } answers { firstArg() }
            service.processWalletReport(
                wallet = wallet,
                walletReport = walletReport,
                reportMonth = reportMonth,
                monthlyWalletService = monthService
            )
            val slot = slot<Wallet>()
            verify(exactly = 1) { repository.save(capture(slot)) }
            slot.captured.should {
                it.balance shouldBeEqual wallet.balance.plus(walletReport.balanceContribution)
                it.balanceDaytrade shouldBeEqual wallet.balanceDaytrade.plus(walletReport.daytradeBalanceContribution)
                it.withdrawn shouldBeEqual wallet.withdrawn.plus(walletReport.withdrawnContribution)
                it.withdrawnDaytrade shouldBeEqual wallet.withdrawnDaytrade.plus(walletReport.daytradeWithdrawnContribution)
            }
            verify(exactly = 0) { monthRepository.save(any()) }
            verify(exactly = 0) { monthService.getMonthlyWalletByMonth(any()) }
        }

        "should process wallet report when different month" {
            val reportMonth = now().minusMonths(1).atStartOfMonth()
            val wallet = wallet().copy(walletMonth = now().atStartOfMonth())
            val monthWallet = monthlyWallet().copy(walletMonth = reportMonth, walletId = wallet.id)
            val walletReport = random<AccountingService.WalletReport>()
            val monthService = mockk<MonthlyWalletServiceFacadeImpl> {
                every { getMonthlyWalletByMonth(reportMonth) } returns monthWallet
            }
            every { monthRepository.save(any()) } answers { firstArg() }
            service.processWalletReport(
                wallet = wallet,
                walletReport = walletReport,
                reportMonth = reportMonth,
                monthlyWalletService = monthService
            )
            val slot = slot<MonthlyWallet>()
            verify(exactly = 1) { monthService.getMonthlyWalletByMonth(any()) }
            verify(exactly = 1) { monthRepository.save(capture(slot)) }
            slot.captured.should {
                it.balance shouldBeEqual monthWallet.balance.plus(walletReport.balanceContribution)
                it.balanceDaytrade shouldBeEqual monthWallet.balanceDaytrade.plus(walletReport.daytradeBalanceContribution)
                it.withdrawn shouldBeEqual monthWallet.withdrawn.plus(walletReport.withdrawnContribution)
                it.withdrawnDaytrade shouldBeEqual monthWallet.withdrawnDaytrade.plus(walletReport.daytradeWithdrawnContribution)
            }
        }

        "should process wallet report when different month and monthlyWallet did not exist beforehand" {
            val reportMonth = now().minusMonths(1).atStartOfMonth()
            val wallet = wallet().copy(walletMonth = now().atStartOfMonth())
            val monthWallet = wallet.toMonthlyWallet()
            val walletReport = random<AccountingService.WalletReport>()
            val monthService = mockk<MonthlyWalletServiceFacadeImpl> {
                every { getMonthlyWalletByMonth(reportMonth) } returns null
            }
            every { monthRepository.save(any()) } answers { firstArg() }
            service.processWalletReport(
                wallet = wallet,
                walletReport = walletReport,
                reportMonth = reportMonth,
                monthlyWalletService = monthService
            )
            val slot = slot<MonthlyWallet>()
            verify(exactly = 1) { monthService.getMonthlyWalletByMonth(any()) }
            verify(exactly = 1) { monthRepository.save(capture(slot)) }
            slot.captured.should {
                it.balance shouldBeEqual monthWallet.balance.plus(walletReport.balanceContribution)
                it.balanceDaytrade shouldBeEqual monthWallet.balanceDaytrade.plus(walletReport.daytradeBalanceContribution)
                it.withdrawn shouldBeEqual monthWallet.withdrawn.plus(walletReport.withdrawnContribution)
                it.withdrawnDaytrade shouldBeEqual monthWallet.withdrawnDaytrade.plus(walletReport.daytradeWithdrawnContribution)
            }
        }

    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    private fun Wallet.toMonthlyWallet() = MonthlyWallet(
        balanceDaytrade = this.balanceDaytrade,
        walletMonth = this.walletMonth,
        client = this.client
    )

    private fun monthlyWallet() = random<MonthlyWallet>().copy(
        balance = random(),
        balanceDaytrade = random(),
        withdrawnDaytrade = random(),
        withdrawn = random()
    )

    private fun wallet() = random<Wallet>().copy(
        balance = random(),
        balanceDaytrade = random(),
        withdrawnDaytrade = random(),
        withdrawn = random()
    )

}