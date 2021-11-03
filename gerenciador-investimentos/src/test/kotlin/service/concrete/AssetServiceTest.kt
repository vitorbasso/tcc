package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.IAssetRepository
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AssetService
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
import utils.EasyRandomWrapper.random
import utils.shouldBeEqual
import java.math.BigDecimal

class AssetServiceTest : StringSpec() {

    private val assetRespository = mockk<IAssetRepository>()
    private val service = AssetService(assetRespository)

    init {

        "should find asset" {
            val asset = asset()
            every { assetRespository.findByWalletAndStock(any(), any()) } returns asset
            shouldNotThrowAny {
                val result = service.getAsset(random(), random())
                result shouldBe asset
            }
            verify(exactly = 1) { assetRespository.findByWalletAndStock(any(), any()) }
        }

        "should throw exception if asset not found" {
            every { assetRespository.findByWalletAndStock(any(), any()) } returns null
            shouldThrow<CustomEntityNotFoundException> { service.getAsset(random(), random()) }
            verify(exactly = 1) { assetRespository.findByWalletAndStock(any(), any()) }
        }

        "should find asset through getAssetNullable" {
            val asset = asset()
            every { assetRespository.findByWalletAndStock(any(), any()) } returns asset
            shouldNotThrowAny {
                val result = service.getAssetNullable(random(), random())
                result shouldBe asset
            }
            verify(exactly = 1) { assetRespository.findByWalletAndStock(any(), any()) }
        }

        "should not throw exception if asset not found through getAssetNullable" {
            every { assetRespository.findByWalletAndStock(any(), any()) } returns null
            shouldNotThrowAny {
                service.getAssetNullable(random(), random()).shouldBeNull()
            }
            verify(exactly = 1) { assetRespository.findByWalletAndStock(any(), any()) }
        }

        "should pass to repository to delete asset" {
            val asset = asset()
            every { assetRespository.delete(any()) } just runs
            service.deleteAsset(asset)
            val slot = slot<Asset>()
            verify(exactly = 1) { assetRespository.delete(capture(slot)) }
            slot.captured shouldBe asset
        }

        "should pass to repository to save asset"{
            val asset = asset()
            every { assetRespository.save(any()) } returns asset
            service.saveAsset(asset)
            val slot = slot<Asset>()
            verify(exactly = 1) { assetRespository.save(capture(slot)) }
            slot.captured shouldBe asset
        }

        "should pass to repository to save asset with amount 0"{
            val asset = asset().copy(amount = 0)
            every { assetRespository.save(any()) } returns asset
            service.saveAsset(asset)
            val slot = slot<Asset>()
            verify(exactly = 1) { assetRespository.save(capture(slot)) }
            slot.captured shouldBe asset
            slot.captured.averageCost shouldBeEqual BigDecimal.ZERO
        }

    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }

    private fun asset() = random<Asset>().copy(
        averageCost = random(),
        lifetimeBalance = random()
    )

}