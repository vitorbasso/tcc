package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.AccountingOperation
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.WalletServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfDay
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import utils.EasyRandomWrapper.random
import utils.now
import utils.shouldBeEqual
import java.math.BigDecimal
import java.time.LocalDateTime

class AccountingServiceTest : StringSpec() {

    private val walletServiceMock = mockk<WalletServiceFacadeImpl> {
        every { processAccountantReport(any(), any()) } answers { secondArg() }
    }
    private val service = AccountingService(listOf(walletServiceMock))

    init {
        "should call subscribers with results" {
            val transaction = transaction()
            service.accountFor(transaction, random())
            verify(exactly = 1) { walletServiceMock.processAccountantReport(transaction, any()) }
        }

        "new transaction should be daytrade if different type" {
            val today = now()
            val transaction = transaction(transactionDate = today, type = TransactionType.SELL)
            val staleTransactions = listOf(
                transaction(transactionDate = today)
            )
            val result = service.accountFor(transaction, staleTransactions)
            result.transactionsReport.should {
                assertCorrectDaytradeQuantity(it, transaction.id)
            }
        }

        "new transaction should have correct daytrade quantity"{
            val today = now()
            val baseTransaction = transaction(transactionDate = today, type = TransactionType.SELL)
            val transactions = listOf(
                baseTransaction.copy(quantity = 10),
                baseTransaction.copy(quantity = 15),
                baseTransaction.copy(quantity = 20)
            )
            val staleTransactions = listOf(
                transaction(transactionDate = today, quantity = 15)
            )
            transactions.forAll { transaction ->
                val result = service.accountFor(transaction, staleTransactions)
                result.transactionsReport.should {
                    assertCorrectDaytradeQuantity(it, baseTransaction.id)
                }
            }
        }

        "new transaction should not be daytrade if different day or same type"{
            val today = now()
            val transactions = listOf(
                transaction(transactionDate = today, type = TransactionType.SELL), //different day, different type
                transaction(transactionDate = today.minusDays(2), type = TransactionType.SELL),
                transaction(transactionDate = today.minusMonths(1), type = TransactionType.SELL),
                transaction(transactionDate = today.minusYears(1), type = TransactionType.SELL),
                transaction(transactionDate = today, type = TransactionType.BUY), //different day, same type
                transaction(transactionDate = today.minusDays(2), type = TransactionType.BUY),
                transaction(transactionDate = today.minusMonths(1), type = TransactionType.BUY),
                transaction(transactionDate = today.minusYears(1), type = TransactionType.BUY),
                transaction(transactionDate = today.minusDays(1)), //same day, different type
            )
            val staleTransactions = listOf(
                transaction(transactionDate = today.minusDays(1), quantity = 15)
            )
            transactions.forAll { transaction ->
                val result = service.accountFor(transaction, staleTransactions)
                result.transactionsReport.should {
                    assertCorrectDaytradeQuantity(it, transaction.id)
                }
            }
        }

        "daytrade quantity should be correct if stale transaction already had some"{
            val today = now()
            val baseTransaction = transaction(transactionDate = today, type = TransactionType.SELL)
            val transactions = listOf(
                baseTransaction.copy(quantity = 10),
                baseTransaction.copy(quantity = 15),
                baseTransaction.copy(quantity = 20)
            )
            val offset = 3L
            val staleTransactions = listOf(
                transaction(transactionDate = today, quantity = 15, daytradeQuantity = offset),
                transaction(
                    transactionDate = today,
                    quantity = offset,
                    type = TransactionType.SELL,
                    daytradeQuantity = offset
                )
            )
            transactions.forAll { transaction ->
                val result = service.accountFor(transaction, staleTransactions)
                result.transactionsReport.should { transactionReport ->
                    transactionReport shouldHaveSize 3
                    val (new, stale) = transactionReport.partition { it.id == transaction.id }
                    val newTransaction = new.single()
                    val staleTransaction = stale.find { it.quantity > offset }.shouldNotBeNull()
                    if (newTransaction.quantity < staleTransaction.quantity) {
                        newTransaction.daytradeQuantity shouldBe newTransaction.quantity
                        staleTransaction.daytradeQuantity shouldBe newTransaction.quantity + offset
                    } else {
                        newTransaction.daytradeQuantity shouldBe staleTransaction.quantity - offset
                        staleTransaction.daytradeQuantity shouldBe staleTransaction.quantity
                    }
                }
            }
        }

        "should account correctly for lifetime balance change when adding transaction" {
            val today = now()
            val transactions = listOf(
                transaction(value = BigDecimal.TEN), //remove 10
                transaction(value = BigDecimal.TEN, type = TransactionType.SELL), // add 10
                transaction(
                    value = BigDecimal.TEN,
                    transactionDate = today,
                    type = TransactionType.SELL
                ) // add 10 daytrade

            )
            val staleTransaction = transaction(transactionDate = today)
            transactions.forAll { transaction ->
                val result = service.accountFor(transaction, listOf(staleTransaction))
                when (transaction.type) {
                    TransactionType.BUY -> result.lifetimeBalanceChange shouldBe -transaction.value
                    TransactionType.SELL -> result.lifetimeBalanceChange shouldBe transaction.value
                }
            }
        }

        "should account correctly for lifetime balance change when removing transaction" {
            val today = now()
            val transactions = listOf(
                transaction(value = BigDecimal.TEN), //remove 10
                transaction(value = BigDecimal.TEN, type = TransactionType.SELL), // add 10
                transaction(
                    value = BigDecimal.TEN,
                    transactionDate = today,
                    type = TransactionType.SELL
                ) // add 10 daytrade
            )
            val staleTransaction = transaction(transactionDate = today)
            transactions.forAll { transaction ->
                val result =
                    service.accountFor(
                        transaction = transaction,
                        staleTransactions = listOf(transaction, staleTransaction),
                        accountingOperation = AccountingOperation.REMOVE_TRANSACTION
                    )
                when (transaction.type) {
                    TransactionType.BUY -> result.lifetimeBalanceChange shouldBe transaction.value
                    TransactionType.SELL -> result.lifetimeBalanceChange shouldBe -transaction.value
                }
            }
        }

        "should account correctly for lifetime balance change when removing asset" {
            val today = now()
            val asset = random<Asset>()
            val transaction = transaction(value = BigDecimal.TEN, asset = asset) // remove 10
            val staleTransactions = listOf(
                transaction,
                transaction(value = BigDecimal.TEN, asset = asset), //remove 10
                transaction(value = BigDecimal.TEN, type = TransactionType.SELL, asset = asset), // add 10
                transaction(
                    value = BigDecimal.TEN,
                    transactionDate = today,
                    type = TransactionType.SELL,
                    asset = asset
                ), // add 10 daytrade
                transaction(transactionDate = today, value = BigDecimal.TEN, asset = asset) // remove 10 daytrade
            )
            service.accountFor(
                transaction = transaction,
                staleTransactions = staleTransactions,
                accountingOperation = AccountingOperation.REMOVE_ASSET
            ).lifetimeBalanceChange shouldBe BigDecimal.TEN
        }

        "should calculate correct asset average cost"{
            val asset = random<Asset>()
            val date = now().minusDays(5)
            var transaction =
                transaction(quantity = 10, value = BigDecimal("100"), transactionDate = date, asset = asset) //10 cada
            var result = service.accountFor(transaction, listOf())
            result.assetReport shouldBeEqual BigDecimal.TEN
            var staleTransactions = result.transactionsReport
            transaction = transaction(
                quantity = 15,
                value = BigDecimal("300"),
                transactionDate = date.plusDays(1),
                asset = asset
            ) //20 cada
            result = service.accountFor(transaction, staleTransactions)
            result.assetReport shouldBeEqual BigDecimal("16") // 10 por 10 cada e 15 por 20 cada -> 25 por 400 -> 16 cada
            staleTransactions = result.transactionsReport
            transaction = transaction(
                quantity = 10,
                value = BigDecimal("300"),
                transactionDate = date.plusDays(2),
                asset = asset
            ) // 30 cada
            result = service.accountFor(transaction, staleTransactions)
            result.assetReport shouldBeEqual BigDecimal("20") // 25 por 400 e 10 por 300 -> 35 por 700
            staleTransactions = result.transactionsReport
            transaction = transaction(
                quantity = 15,
                value = BigDecimal("750"),
                transactionDate = date.plusDays(3),
                type = TransactionType.SELL,
                asset = asset
            ) // vendendo 15 -> sobra 20 por 20 cada
            result = service.accountFor(transaction, staleTransactions)
            result.assetReport shouldBeEqual BigDecimal("20") // como foi vendido, nao altera media
            staleTransactions = result.transactionsReport
            transaction = transaction(
                quantity = 10,
                value = BigDecimal("500"),
                transactionDate = date.plusDays(4).minusSeconds(10),
                asset = asset
            ) //50 cada
            result = service.accountFor(transaction, staleTransactions)
            result.assetReport shouldBeEqual BigDecimal("30") // 20 por 20 cada -> 400 + 10 por 50 cada -> 500 = 30 por 900
            staleTransactions = result.transactionsReport
            transaction = transaction(
                quantity = 10,
                value = BigDecimal("500"),
                transactionDate = date.plusDays(4),
                type = TransactionType.SELL,
                asset = asset
            ) //50 cada
            result = service.accountFor(transaction, staleTransactions)
            result.assetReport shouldBeEqual BigDecimal("20") // transforma a transaction acima em daytrade e remove a alteração dela no preco medio
        }

    }

    private fun assertCorrectDaytradeQuantity(transactionReport: List<Transaction>, id: Long) {
        transactionReport shouldHaveSize 2
        val (new, stale) = transactionReport.partition {
            it.id == id
        }
        val newTransaction = new.single()
        val staleTransaction = stale.single()
        if (!isDaytrade(newTransaction, staleTransaction)) {
            newTransaction.daytradeQuantity shouldBe 0
            staleTransaction.daytradeQuantity shouldBe 0
        } else if (newTransaction.quantity <= staleTransaction.quantity) {
            newTransaction.daytradeQuantity shouldBe newTransaction.quantity
            staleTransaction.daytradeQuantity shouldBe newTransaction.quantity
        } else {
            newTransaction.daytradeQuantity shouldBe staleTransaction.quantity
            staleTransaction.daytradeQuantity shouldBe staleTransaction.quantity
        }
    }

    private fun isDaytrade(newTransaction: Transaction, staleTransaction: Transaction) =
        newTransaction.transactionDate.atStartOfDay().isEqual(staleTransaction.transactionDate.atStartOfDay())
            && newTransaction.type != staleTransaction.type

    private fun transaction(
        type: TransactionType = TransactionType.BUY,
        quantity: Long = (1..100L).random(),
        value: BigDecimal = BigDecimal.TEN,
        transactionDate: LocalDateTime = random(),
        checkingValue: BigDecimal = BigDecimal.ZERO,
        checkingQuantity: Long = 0,
        daytradeQuantity: Long = 0,
        asset: Asset = random()
    ) = Transaction(
        id = random(),
        type = type,
        quantity = quantity,
        value = value,
        transactionDate = transactionDate,
        checkingValue = checkingValue,
        checkingQuantity = checkingQuantity,
        daytradeQuantity = daytradeQuantity,
        asset = asset
    )

}