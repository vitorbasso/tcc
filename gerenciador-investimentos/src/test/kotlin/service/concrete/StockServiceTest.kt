package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Stock
import com.vitorbasso.gerenciadorinvestimentos.dto.response.WalletDto
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.concrete.StockRepository
import com.vitorbasso.gerenciadorinvestimentos.service.IWalletService
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.StockService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import utils.EasyRandomWrapper.random

class StockServiceTest : StringSpec() {

    private val repository = mockk<StockRepository>()
    private val walletService = mockk<IWalletService>()
    private val service = StockService(repository, walletService)

    init {
        "should get stock" {
            val stock = random<Stock>()
            every { repository.findByTicker(any()) } returns stock
            shouldNotThrowAny {
                val result = service.getStock(random())
                result shouldBe stock
            }
            verify(exactly = 1) { repository.findByTicker(any()) }
        }

        "should throw when stock not found" {
            every { repository.findByTicker(any()) } returns null
            shouldThrow<CustomEntityNotFoundException> {
                service.getStock(random())
            }
            verify(exactly = 1) { repository.findByTicker(any()) }
        }

        "should get stocks batch" {
            val stocks = random<List<Stock>>()
            val tickers = random<List<String>>()
            every { repository.findByTickerBatch(any()) } returns stocks
            shouldNotThrowAny {
                val result = service.getStocksBatch(tickers)
                result shouldBe stocks
            }
            val slot = slot<List<String>>()
            verify(exactly = 1) { repository.findByTickerBatch(capture(slot)) }
            slot.captured.last() shouldBe "^BVSP"
        }

        "should not throw when none found in get stocks batch" {
            every { repository.findByTickerBatch(any()) } returns emptyList()
            shouldNotThrowAny {
                val result = service.getStocksBatch(random())
                result.shouldBeEmpty()
            }
            verify(exactly = 1) { repository.findByTickerBatch(any()) }
        }

        "should get all stocks from a wallet" {
            val wallet = random<WalletDto>()
            val stocks = random<List<Stock>>()
            every { walletService.getWallet() } returns wallet
            every { repository.findByTickerBatch(any()) } returns stocks

            val result = service.getAllStocks()
            result shouldBe stocks
            val tickers = wallet.stockAssets.map { it.stockSymbol } + listOf("^BVSP")
            verify(exactly = 1) { repository.findByTickerBatch(tickers) }
        }

        "should not throw when none found in get all stocks from a wallet" {
            val wallet = random<WalletDto>()
            every { walletService.getWallet() } returns wallet
            every { repository.findByTickerBatch(any()) } returns emptyList()

            val result = service.getAllStocks()
            result.shouldBeEmpty()
            val tickers = wallet.stockAssets.map { it.stockSymbol } + listOf("^BVSP")
            verify(exactly = 1) { repository.findByTickerBatch(tickers) }
        }

        "should get stocks starting with" {
            val stocks = random<List<Stock>>()
            every { repository.findByTickerStartsWith(any()) } returns stocks
            service.getStockStartingWith(random()) shouldBe stocks
            verify(exactly = 1) { repository.findByTickerStartsWith(any()) }
        }
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

}