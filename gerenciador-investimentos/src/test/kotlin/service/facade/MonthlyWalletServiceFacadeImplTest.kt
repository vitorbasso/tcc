package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.MonthlyWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import utils.EasyRandomWrapper.random
import java.time.LocalDate

class MonthlyWalletServiceFacadeImplTest : StringSpec() {

    private val walletService = mockk<MonthlyWalletService>()
    private val service = MonthlyWalletServiceFacadeImpl(walletService)

    private val client = random<Client>()

    init {
        "should get monthly wallets" {
            val monthlyWallets = listOf<MonthlyWallet>(
                random(),
                random(),
                random(),
            )
            every { walletService.getMonthlyWallets(client.id) } returns monthlyWallets
            service.getMonthlyWallets() shouldBe monthlyWallets
            verify(exactly = 1) { SecurityContextUtil.getClientDetails() }
            verify(exactly = 1) { walletService.getMonthlyWallets(any()) }
        }

        "should get one monthly wallet"{
            val monthWallet = random<MonthlyWallet>()
            every { walletService.getMonthlyWallet(any(), client.id, any()) } returns monthWallet
            service.getMonthlyWallet(random()) shouldBe monthWallet
            verify(exactly = 1) { SecurityContextUtil.getClientDetails() }
            verify(exactly = 1) { walletService.getMonthlyWallet(any(), any(), any()) }
        }

        "should get monthly wallet by month" {
            val month = random<LocalDate>()
            val wallet = random<MonthlyWallet>()
            every { walletService.getMonthlyWalletByMonth(month.atStartOfMonth(), any()) } returns wallet
            service.getMonthlyWalletByMonth(month) shouldBe wallet
            verify(exactly = 1) { SecurityContextUtil.getClientDetails() }
            verify(exactly = 1) { walletService.getMonthlyWalletByMonth(any(), any()) }
        }

        "should delete monthly wallet" {
            val walletId = random<Long>()
            val wallet = random<MonthlyWallet>()
            every { walletService.getMonthlyWallet(walletId, client.id, any()) } returns wallet
            every { walletService.deleteMonthlyWallet(wallet) } just runs
            service.deleteMonthlyWallet(walletId)
            verify(exactly = 1) { SecurityContextUtil.getClientDetails() }
            verify(exactly = 1) { walletService.getMonthlyWallet(walletId, client.id, any()) }
            verify(exactly = 1) { walletService.deleteMonthlyWallet(wallet) }
        }
    }

    override fun beforeEach(testCase: TestCase) {
        mockkObject(SecurityContextUtil)
        every { SecurityContextUtil.getClientDetails() } returns client
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

}