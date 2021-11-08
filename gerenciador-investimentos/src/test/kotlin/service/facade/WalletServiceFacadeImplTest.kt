package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.WalletService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.MonthlyWalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.service.facade.WalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import utils.EasyRandomWrapper.random
import java.time.LocalDate
import java.time.LocalDateTime

class WalletServiceFacadeImplTest : StringSpec() {

    private val walletService = mockk<WalletService>()
    private val monthlyWalletService = mockk<MonthlyWalletServiceFacadeImpl>()
    private val service = WalletServiceFacadeImpl(walletService, monthlyWalletService)

    init {
        "should get wallet" {
            val client = random<Client>()
            val wallet = random<Wallet>().copy(client = client, walletMonth = LocalDateTime.now().atStartOfMonth())
            mockkObject(SecurityContextUtil)
            every { SecurityContextUtil.getClientDetails() } returns client
            every { walletService.getWallet(client.id, any()) } returns wallet

            val result = service.getWallet()

            result shouldBe wallet
            verify(exactly = 1) { walletService.getWallet(client.id, any()) }
            verify(exactly = 0) { walletService.enforceWalletMonth(any()) }
        }

        "should enforce wallet month" {
            val client = random<Client>()
            val wallet = random<Wallet>().copy(
                client = client,
                walletMonth = LocalDateTime.now().minusMonths(1).atStartOfMonth()
            )
            val newWallet = Wallet(client = client, walletMonth = LocalDate.now().atStartOfMonth())
            mockkObject(SecurityContextUtil)
            every { SecurityContextUtil.getClientDetails() } returns client
            every { walletService.getWallet(client.id, any()) } returns wallet
            every { walletService.enforceWalletMonth(wallet) } returns newWallet

            val result = service.getWallet()

            result shouldBe newWallet
            verify(exactly = 1) { walletService.getWallet(client.id, any()) }
            verify(exactly = 1) { walletService.enforceWalletMonth(wallet) }
        }

        "should delete wallet"{
            val client = random<Client>()
            val wallet = random<Wallet>().copy(
                client = client,
                walletMonth = LocalDateTime.now().atStartOfMonth()
            )
            mockkObject(SecurityContextUtil)
            every { SecurityContextUtil.getClientDetails() } returns client
            every { walletService.getWallet(client.id, any()) } returns wallet
            every { walletService.deleteWallet(wallet) } just runs

            service.deleteWallet(wallet.id)

            verifyOrder {
                walletService.getWallet(client.id, any())
                walletService.deleteWallet(wallet)
            }
        }

        "should process accountant report "{
            val wallet = random<Wallet>().copy(walletMonth = LocalDate.now().atStartOfMonth())
            val asset = random<Asset>().copy(wallet = wallet)
            val transaction = random<Transaction>().copy(asset = asset)
            val walletsReport = mapOf<LocalDate, AccountingService.WalletReport>(
                LocalDate.now().atStartOfMonth() to random(),
                LocalDate.now().minusMonths(1).atStartOfMonth() to random(),
                LocalDate.now().minusMonths(2).atStartOfMonth() to random(),
            )
            val report = random<AccountingService.AccountantReport>().copy(walletsReport = walletsReport)
            every {
                walletService.processWalletReport(
                    wallet,
                    any(),
                    any(),
                    any()
                )
            } just runs
            val result = service.processAccountantReport(transaction, report)
            result shouldBe report
            val reportSlot = mutableListOf<AccountingService.WalletReport>()
            val monthSlot = mutableListOf<LocalDate>()
            verify {
                walletService.processWalletReport(
                    wallet,
                    capture(reportSlot),
                    capture(monthSlot),
                    any()
                )
            }
            reportSlot shouldContainExactlyInAnyOrder walletsReport.values
            monthSlot shouldContainExactlyInAnyOrder walletsReport.keys
        }

    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

}