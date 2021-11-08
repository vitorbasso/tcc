package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import utils.EasyRandomWrapper.random
import utils.transaction

class TransactionServiceTest : StringSpec() {

    private val repository = mockk<ITransactionRepository>()
    private val service = TransactionService(repository)

    init {
        "should get transaction" {
            val clientId = random<Long>()
            val transaction = transaction().copy(
                asset = random<Asset>().copy(
                    wallet = random<Wallet>().copy(
                        client = random<Client>().copy(id = clientId)
                    )
                )
            )
            every { repository.findByIdOrNull(any()) } returns transaction
            shouldNotThrowAny {
                val result = service.getTransaction(random(), clientId)
                result shouldBe transaction
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should throw when not same clientId" {
            val clientId = random<Long>()
            every { repository.findByIdOrNull(any()) } returns transaction()
            shouldThrow<CustomEntityNotFoundException> {
                service.getTransaction(random(), clientId)
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should throw when not found transaction" {
            every { repository.findByIdOrNull(any()) } returns null
            shouldThrow<CustomEntityNotFoundException> {
                service.getTransaction(random(), random())
            }
            verify(exactly = 1) { repository.findByIdOrNull(any()) }
        }

        "should save transaction" {
            val transaction = transaction()
            every { repository.save(any()) } answers { firstArg() }
            val result = service.save(transaction)
            result shouldBe transaction
            verify(exactly = 1) { repository.save(transaction) }
        }

        "should save all transactions" {
            val transactions = listOf(
                transaction(),
                transaction(),
                transaction(),
            )
            every { repository.saveAll(any<List<Transaction>>()) } returns transactions
            val result = service.saveAll(transactions)
            result shouldBe transactions
            verify(exactly = 1) { repository.saveAll(transactions) }
        }

        "should find all by asset" {
            val asset = random<Asset>()
            every { repository.findAllByAssetOrderByTransactionDate(any()) } returns random()
            service.findAllByAsset(asset)
            verify(exactly = 1) { repository.findAllByAssetOrderByTransactionDate(asset) }
        }

        "should find from one before transaction date" {
            val transactions = listOf(
                transaction(),
                transaction(),
                transaction()
            )
            every { repository.findAllFromTransactionBeforeTransactionDate(any(), any()) } returns transactions
            val result = service.findFromOneBeforeTransactionDate(random())
            result shouldBe transactions
            verify(exactly = 1) { repository.findAllFromTransactionBeforeTransactionDate(any(), any()) }
            verify(exactly = 0) { repository.findAllByAssetOrderByTransactionDate(any()) }
        }

        "should not find from one before transaction date" {
            val transactions = listOf(
                transaction(),
                transaction(),
                transaction(),
            )
            every { repository.findAllByAssetOrderByTransactionDate(any()) } returns transactions
            every { repository.findAllFromTransactionBeforeTransactionDate(any(), any()) } returns emptyList()
            val result = service.findFromOneBeforeTransactionDate(random())
            result shouldBe transactions
            verify(exactly = 1) { repository.findAllFromTransactionBeforeTransactionDate(any(), any()) }
            verify(exactly = 1) { repository.findAllByAssetOrderByTransactionDate(any()) }
        }

        "should delete transaction" {
            every { repository.delete(any()) } just runs
            service.deleteTransaction(random())
            verify(exactly = 1) { repository.delete(any()) }
        }
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

}