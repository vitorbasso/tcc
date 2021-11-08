package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.MonthlyWallet
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IMonthlyWalletRepository
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.MonthlyWalletService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import utils.EasyRandomWrapper.random
import java.time.LocalDate

class MonthlyWalletServiceTest : StringSpec() {

    private val repository = mockk<IMonthlyWalletRepository>()
    private val service = MonthlyWalletService(repository)

    init {

        "should get all monthly wallets by id" {
            val monthlyWallets = listOf<MonthlyWallet>(
                monthlyWallet(),
                monthlyWallet(),
                monthlyWallet(),
            )
            every { repository.findAllByClientId(any()) } returns monthlyWallets
            val result = service.getMonthlyWallets(random())
            result shouldBe monthlyWallets
            verify(exactly = 1) { repository.findAllByClientId(any()) }
        }

        "should get specific monthly wallet by id" {
            val clientId = random<Long>()
            val monthlyWallet = monthlyWallet().copy(
                client = random<Client>().copy(
                    id = clientId
                )
            )
            every { repository.findByIdOrNull(any()) } returns monthlyWallet
            shouldNotThrowAny {
                val result = service.getMonthlyWallet(
                    random(),
                    clientId
                )
                result shouldBe monthlyWallet
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should throw when monthly wallet not found by id" {
            every { repository.findByIdOrNull(any()) } returns null
            shouldThrow<CustomEntityNotFoundException> {
                service.getMonthlyWallet(
                    random(),
                    random()
                )
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should throw when specific monthly wallet does not match clientId" {
            val clientId = random<Long>()
            every { repository.findByIdOrNull(any()) } returns monthlyWallet()
            shouldThrow<CustomEntityNotFoundException> {
                service.getMonthlyWallet(
                    random(),
                    clientId
                )
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should get specific monthly wallet by month" {
            val clientId = random<Long>()
            val month = random<LocalDate>()
            val monthlyWallet = monthlyWallet().copy(
                client = random<Client>().copy(
                    id = clientId
                ),
                walletMonth = month
            )
            every { repository.findByWalletMonthAndClientId(any(), any()) } returns monthlyWallet
            shouldNotThrowAny {
                val result = service.getMonthlyWalletByMonth(
                    month,
                    clientId
                )
                result shouldBe monthlyWallet
            }
            verify(exactly = 1) { repository.findByWalletMonthAndClientId(any(), any()) }
        }

        "should not throw when monthly wallet not found by month" {
            every { repository.findByWalletMonthAndClientId(any(), any()) } returns null
            shouldNotThrowAny {
                service.getMonthlyWalletByMonth(
                    random(),
                    random()
                ).shouldBeNull()
            }
            verify(exactly = 1) { repository.findByWalletMonthAndClientId(any(), any()) }
        }

        "should throw when specific monthly wallet does not match clientId by month" {
            val clientId = random<Long>()
            every { repository.findByWalletMonthAndClientId(any(), any()) } returns random()
            shouldThrow<CustomBadRequestException> {
                service.getMonthlyWalletByMonth(
                    random(),
                    clientId
                )
            }
            verify(exactly = 1) { repository.findByWalletMonthAndClientId(any(), any()) }
        }

        "should pass to repository to delete" {
            val toDelete = monthlyWallet()
            every { repository.delete(any()) } just runs
            service.deleteMonthlyWallet(toDelete)
            val slot = slot<MonthlyWallet>()
            verify(exactly = 1) { repository.delete(capture(slot)) }
            slot.captured shouldBe toDelete
        }

    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    private fun monthlyWallet() = random<MonthlyWallet>().copy(
        balanceDaytrade = random(),
        balance = random(),
        withdrawn = random(),
        withdrawnDaytrade = random()
    )

}