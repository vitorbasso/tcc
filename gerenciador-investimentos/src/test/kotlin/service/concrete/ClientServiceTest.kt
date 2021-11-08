package service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomBadRequestException
import com.vitorbasso.gerenciadorinvestimentos.repository.IClientRepository
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
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
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import utils.EasyRandomWrapper.random

class ClientServiceTest : StringSpec() {
    private val clientRepository = mockk<IClientRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val service = ClientService(clientRepository, passwordEncoder)

    init {
        "should save client if not exists" {
            val clientToSave = random<Client>()
            every { passwordEncoder.encode(any()) } answers { "encoded.${firstArg<String>()}" }
            every { clientRepository.save(any()) } answers { firstArg() }
            every { clientRepository.existsByEmail(any()) } returns false
            shouldNotThrowAny {
                val result = service.saveClient(clientToSave)
                result.password shouldBe "encoded.${clientToSave.password}"
                result.id shouldBe clientToSave.id
                result.email shouldBe clientToSave.email
                result.name shouldBe clientToSave.name
                result.wallet shouldBe clientToSave.wallet
            }
            verify(exactly = 1) { clientRepository.save(any()) }
        }

        "should throw if trying to save a client that already exists" {
            val clientToSave = random<Client>()
            every { clientRepository.existsByEmail(any()) } returns true
            shouldThrow<CustomBadRequestException> {
                service.saveClient(clientToSave)
            }
            verify(exactly = 0) { clientRepository.save(any()) }
        }

        "should update client" {
            val clientToUpdate = random<Client>()
            val updateRequest = random<ClientUpdateRequest>()
            every { clientRepository.save(any()) } answers { firstArg() }
            val result = service.updateClient(clientToUpdate, updateRequest)
            result.password shouldBe clientToUpdate.password
            result.id shouldBe clientToUpdate.id
            result.email shouldBe clientToUpdate.email
            result.name shouldBe updateRequest.name
            result.wallet shouldBe clientToUpdate.wallet

            verify(exactly = 1) { clientRepository.save(any()) }
        }

        "should delete client" {
            val clientToDelete = random<Client>()
            every { clientRepository.delete(any()) } just runs

            service.deleteClient(clientToDelete)
            val slot = slot<Client>()
            verify(exactly = 1) { clientRepository.delete(capture(slot)) }
            slot.captured shouldBe clientToDelete
        }

    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        clearAllMocks()
    }
}