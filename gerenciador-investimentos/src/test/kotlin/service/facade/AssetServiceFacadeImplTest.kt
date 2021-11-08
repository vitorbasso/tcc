package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Wallet
import com.vitorbasso.gerenciadorinvestimentos.enum.AccountingOperation
import com.vitorbasso.gerenciadorinvestimentos.enum.TransactionType
import com.vitorbasso.gerenciadorinvestimentos.service.IStockService
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.TransactionService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.AssetServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.context.ApplicationContext
import utils.EasyRandomWrapper.random
import utils.now
import utils.transaction
import java.time.LocalDate

class AssetServiceFacadeImplTest : StringSpec() {

    private val wallet = random<Wallet>()
    private val stock = random<Stock>()
    private val assetService = mockk<AssetService>()
    private val walletService = mockk<IWalletService> {
        every { getWallet() } returns wallet
    }
    private val stockService = mockk<IStockService> {
        every { getStock(any()) } returns stock
    }
    private val context = mockk<ApplicationContext>()
    private val service = AssetServiceFacadeImpl(
        assetService = assetService,
        walletService = walletService,
        stockService = stockService,
        context = context
    )

    init {
        "should get asset when already exists" {
            val asset = random<Asset>()
            every { assetService.getAssetNullable(wallet, stock) } returns asset
            service.getAsset(random()) shouldBe asset
            verify(exactly = 1) { walletService.getWallet() }
            verify(exactly = 1) { stockService.getStock(any()) }
            verify(exactly = 1) { assetService.getAssetNullable(wallet, stock) }
            verify(exactly = 0) { assetService.saveAsset(any()) }
        }

        "should create and return asset" {
            val asset = Asset(wallet = wallet, stock = stock)
            every { assetService.getAssetNullable(wallet, stock) } returns null
            every { assetService.saveAsset(asset) } returns asset
            service.getAsset(random()) shouldBe asset
            verify(exactly = 1) { walletService.getWallet() }
            verify(exactly = 1) { stockService.getStock(any()) }
            verify(exactly = 1) { assetService.getAssetNullable(wallet, stock) }
            verify(exactly = 1) { assetService.saveAsset(asset) }
        }

        "should delete asset" {
            val asset = random<Asset>()
            val staleTransactions = listOf(
                transaction(),
                transaction(),
                transaction(),
            )
            every { assetService.deleteAsset(asset) } just runs
            every { assetService.getAsset(wallet, stock) } returns asset
            every {
                context.getBean(
                    "transactionService",
                    TransactionService::class
                )
            } returns mockk<TransactionService> {
                every {
                    findAllByAsset(asset)
                } returns staleTransactions
            }
            every { context.getBean("accountingService", AccountingService::class) } returns mockk<AccountingService> {
                every {
                    accountFor(
                        transaction = Transaction(asset = asset),
                        staleTransactions = staleTransactions,
                        accountingOperation = AccountingOperation.REMOVE_ASSET
                    )
                } returns random()
            }

            service.deleteAsset(random())
            verify(exactly = 1) { assetService.deleteAsset(any()) }
            verify(exactly = 1) { assetService.getAsset(any(), any()) }
            verify(exactly = 1) { walletService.getWallet() }
            verify(exactly = 1) { stockService.getStock(any()) }
            verify(exactly = 2) { context.getBean(any(), any()) }
        }

        "should process accountant report when transaction buy operation add" {
            val asset = asset()
            val transaction = transaction(quantity = 10, asset = asset)
            val report = report()
            every { assetService.saveAsset(any()) } answers { firstArg() }
            val result = service.processAccountantReport(
                transaction,
                report
            )
            result shouldBe report
            val slot = slot<Asset>()
            verify { assetService.saveAsset(capture(slot)) }
            slot.captured.should {
                it.amount shouldBe transaction.asset.amount + transaction.quantity
                it.averageCost shouldBe report.assetReport
                it.lifetimeBalance shouldBe transaction.asset.lifetimeBalance.add(report.lifetimeBalanceChange)
            }
        }

        "should process accountant report when transaction sell operation add" {
            val asset = asset()
            val transaction = transaction(quantity = 10, asset = asset, type = TransactionType.SELL)
            val report = report()
            every { assetService.saveAsset(any()) } answers { firstArg() }
            val result = service.processAccountantReport(
                transaction,
                report
            )
            result shouldBe report
            val slot = slot<Asset>()
            verify { assetService.saveAsset(capture(slot)) }
            slot.captured.should {
                it.amount shouldBe transaction.asset.amount - transaction.quantity
                it.averageCost shouldBe report.assetReport
                it.lifetimeBalance shouldBe transaction.asset.lifetimeBalance.add(report.lifetimeBalanceChange)
            }
        }

        "should process accountant report when transaction buy operation remove" {
            val asset = asset()
            val transaction = transaction(quantity = 10, asset = asset)
            val report = report(accountingOperation = AccountingOperation.REMOVE_TRANSACTION)
            every { assetService.saveAsset(any()) } answers { firstArg() }
            val result = service.processAccountantReport(
                transaction,
                report
            )
            result shouldBe report
            val slot = slot<Asset>()
            verify { assetService.saveAsset(capture(slot)) }
            slot.captured.should {
                it.amount shouldBe transaction.asset.amount - transaction.quantity
                it.averageCost shouldBe report.assetReport
                it.lifetimeBalance shouldBe transaction.asset.lifetimeBalance.add(report.lifetimeBalanceChange)
            }
        }

        "should process accountant report when transaction sell operation remove" {
            val asset = asset()
            val transaction = transaction(quantity = 10, asset = asset, type = TransactionType.SELL)
            val report = report(accountingOperation = AccountingOperation.REMOVE_TRANSACTION)
            every { assetService.saveAsset(any()) } answers { firstArg() }
            val result = service.processAccountantReport(
                transaction,
                report
            )
            result shouldBe report
            val slot = slot<Asset>()
            verify { assetService.saveAsset(capture(slot)) }
            slot.captured.should {
                it.amount shouldBe transaction.asset.amount + transaction.quantity
                it.averageCost shouldBe report.assetReport
                it.lifetimeBalance shouldBe transaction.asset.lifetimeBalance.add(report.lifetimeBalanceChange)
            }
        }

        "should not process accountant report when remove_asset operation" {
            val report = random<AccountingService.AccountantReport>()
                .copy(accountingOperation = AccountingOperation.REMOVE_ASSET)
            service.processAccountantReport(
                random(),
                report
            ) shouldBe report
            verify(exactly = 0) { assetService.saveAsset(any()) }
        }
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks(answers = false)
    }

    private fun asset() = random<Asset>().copy(
        averageCost = random(),
        lifetimeBalance = random()
    )

    private fun walletReport() = AccountingService.WalletReport(
        balanceContribution = random(),
        daytradeBalanceContribution = random(),
        withdrawnContribution = random(),
        daytradeWithdrawnContribution = random()
    )

    private fun report(
        accountingOperation: AccountingOperation = AccountingOperation.ADD_TRANSACTION
    ) = random<AccountingService.AccountantReport>().copy(
        walletsReport = mapOf(
            now().atStartOfMonth() to walletReport(),
            random<LocalDate>() to walletReport(),
            random<LocalDate>() to walletReport(),
        ),
        assetReport = random(),
        transactionsReport = listOf(
            transaction(),
            transaction(),
            transaction(),
        ),
        lifetimeBalanceChange = random(),
        accountingOperation = accountingOperation
    )

}