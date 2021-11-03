package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.ITaxable
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.service.IMonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TaxService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.TaxServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import utils.EasyRandomWrapper.random
import java.time.LocalDate

class TaxServiceFacadeImplTest : StringSpec() {

    private val taxService = mockk<TaxService>()
    private val walletService = mockk<IWalletService>()
    private val monthlyService = mockk<IMonthlyWalletService>()
    private val service = TaxServiceFacadeImpl(
        taxService = taxService,
        walletService = walletService,
        monthlyWalletService = monthlyService
    )

    init {

        "should pass taxables to tax service" {
            val monthlyWallets = listOf(
                monthlyWallet(),
                monthlyWallet(date = LocalDate.now().minusMonths(2)),
                monthlyWallet(date = LocalDate.now().minusMonths(3)),
            )
            val taxInfo = random<TaxService.TaxInfo>()
            val wallet = wallet()
            every { taxService.calculateTax(any()) } returns taxInfo
            every { monthlyService.getMonthlyWallets() } returns monthlyWallets
            every { walletService.getWallet() } returns wallet
            service.getTax(LocalDate.now().atStartOfMonth()) shouldBe taxInfo
            val slot = slot<List<ITaxable>>()
            verify(exactly = 1) { taxService.calculateTax(capture(slot)) }
            slot.captured.should {
                it shouldHaveSize 4
                it shouldContainExactlyInAnyOrder monthlyWallets + wallet
            }
        }

        "should pass taxables that are before month to tax service" {
            val monthlyWallets = listOf(
                monthlyWallet(),
                monthlyWallet(date = LocalDate.now().minusMonths(2)),
                monthlyWallet(date = LocalDate.now().minusMonths(3)),
            )
            val taxInfo = random<TaxService.TaxInfo>()
            val wallet = wallet()
            every { taxService.calculateTax(any()) } returns taxInfo
            every { monthlyService.getMonthlyWallets() } returns monthlyWallets
            every { walletService.getWallet() } returns wallet
            service.getTax(LocalDate.now().minusMonths(2).atStartOfMonth()) shouldBe taxInfo
            val slot = slot<List<ITaxable>>()
            verify(exactly = 1) { taxService.calculateTax(capture(slot)) }
            slot.captured.should {
                it shouldHaveSize 2
                it shouldContainExactlyInAnyOrder monthlyWallets.subList(1, 3)
            }
        }

    }

    private fun monthlyWallet(
        date: LocalDate = LocalDate.now().minusMonths(1)
    ) = random<MonthlyWallet>().copy(
        walletMonth = date.atStartOfMonth(),
        walletId = 1L
    )

    private fun wallet() = random<Wallet>().copy(
        id = 1L,
        walletMonth = LocalDate.now().atStartOfMonth()
    )

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

}