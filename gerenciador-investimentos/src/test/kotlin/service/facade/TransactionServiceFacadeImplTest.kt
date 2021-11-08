package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.enum.AccountingOperation
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomWrongDateException
import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.TransactionServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import utils.EasyRandomWrapper.random
import java.time.LocalDateTime
import java.time.Month

class TransactionServiceFacadeImplTest : StringSpec() {

    private val transactionService = mockk<TransactionService>()
    private val asset = random<Asset>()
    private val assetService = mockk<IAssetService>()
    private val accountingService = mockk<AccountingService>()
    private val service = TransactionServiceFacadeImpl(
        transactionService = transactionService,
        assetService = assetService,
        accountingService = accountingService
    )

    init {

        "should process transaction for correct transaction request" {
            val request = transactionRequest(date = LocalDateTime.of(2021, Month.SEPTEMBER, 20, 16, 32))
            val asset = random<Asset>()
            val staleTransactions = listOf<Transaction>(
                random(),
                random(),
                random()
            )
            val report = random<AccountingService.AccountantReport>()

            every { transactionService.findFromOneBeforeTransactionDate(any()) } returns staleTransactions
            every { transactionService.save(any()) } answers { firstArg() }
            every { accountingService.accountFor(any(), staleTransactions) } returns report
            every { transactionService.saveAll(report.transactionsReport) } returns report.transactionsReport

            val result = service.performTransaction(request)

            verify(exactly = 1) { assetService.getAsset(request.ticker) }
            val transactionSlot = slot<Transaction>()
            verify(exactly = 1) { transactionService.findFromOneBeforeTransactionDate(capture(transactionSlot)) }
            val transaction = transactionSlot.captured
            result shouldBe transaction
            transaction.type shouldBe request.type
            transaction.quantity shouldBe request.quantity
            transaction.value shouldBe request.value
            transaction.asset shouldBe asset
            transaction.transactionDate shouldBe request.date
            verify(exactly = 1) { transactionService.save(transaction) }
            verify(exactly = 1) { accountingService.accountFor(transaction, staleTransactions) }
            verify(exactly = 1) { transactionService.saveAll(report.transactionsReport) }
        }

        "should throw exception if date outside of allowed"{
            val now = LocalDateTime.now().withHour(16)
            val dayOfWeek = now.dayOfWeek.value
            val future = if (dayOfWeek == 6 || dayOfWeek == 7) now.plusDays(4) else now.plusDays(2)
            val dates = listOf(
                LocalDateTime.of(2021, Month.SEPTEMBER, 11, 16, 34), // saturday
                LocalDateTime.of(2021, Month.SEPTEMBER, 12, 16, 34), // sunday
                future, // future in a weekday
            )

            dates.forAll { date ->
                val transaction = transactionRequest(date)
                shouldThrow<CustomWrongDateException> {
                    service.performTransaction(transaction)
                }
                verify(exactly = 1) { assetService.getAsset(transaction.ticker) }
            }
            verify(exactly = 0) { transactionService.findFromOneBeforeTransactionDate(any()) }
            verify(exactly = 0) { transactionService.save(any()) }
            verify(exactly = 0) { accountingService.accountFor(any(), any()) }
            verify(exactly = 0) { transactionService.saveAll(any()) }
        }

        "should get modified transaction" {
            val request = transactionRequest(date = LocalDateTime.of(2021, Month.SEPTEMBER, 20, 16, 32))
            val staleTransactions = listOf<Transaction>(
                random(),
                random(),
                random()
            )
            val transaction = random<Transaction>()
            var report = random<AccountingService.AccountantReport>()
            report = report.copy(transactionsReport = report.transactionsReport + listOf(transaction))

            every { transactionService.findFromOneBeforeTransactionDate(any()) } returns staleTransactions
            every { transactionService.save(any()) } returns transaction
            every { accountingService.accountFor(any(), staleTransactions) } returns report
            every { transactionService.saveAll(report.transactionsReport) } returns report.transactionsReport

            val result = service.performTransaction(request)

            result shouldBe transaction
            verify(exactly = 1) { assetService.getAsset(request.ticker) }
            verify(exactly = 1) { transactionService.findFromOneBeforeTransactionDate(any()) }
            verify(exactly = 1) { transactionService.save(any()) }
            verify(exactly = 1) { accountingService.accountFor(any(), any()) }
            verify(exactly = 1) { transactionService.saveAll(any()) }
        }

        "should delete transaction"{
            val client = random<Client>()
            val transaction = random<Transaction>()
            val staleTransactions = listOf<Transaction>(
                random(),
                random(),
                random()
            )
            val report = random<AccountingService.AccountantReport>()
            mockkObject(SecurityContextUtil)
            every { SecurityContextUtil.getClientDetails() } returns client
            every { transactionService.getTransaction(transaction.id, client.id) } returns transaction
            every { transactionService.findFromOneBeforeTransactionDate(transaction) } returns staleTransactions
            every {
                accountingService.accountFor(
                    transaction,
                    staleTransactions,
                    AccountingOperation.REMOVE_TRANSACTION
                )
            } returns report
            every { transactionService.saveAll(report.transactionsReport) } returns report.transactionsReport
            every { transactionService.deleteTransaction(transaction) } just runs

            service.deleteTransaction(transaction.id)

            verifyOrder {
                transactionService.getTransaction(transaction.id, client.id)
                transactionService.findFromOneBeforeTransactionDate(transaction)
                accountingService.accountFor(
                    transaction,
                    staleTransactions,
                    AccountingOperation.REMOVE_TRANSACTION
                )
                transactionService.saveAll(report.transactionsReport)
                transactionService.deleteTransaction(transaction)
            }
        }

    }

    override fun beforeEach(testCase: TestCase) {
        clearAllMocks()
        every { assetService.getAsset(any()) } returns asset
    }

    private fun transactionRequest(date: LocalDateTime) = random<TransactionRequest>().copy(date = date)

}
