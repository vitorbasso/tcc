package service.facade

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.dto.request.ClientUpdateRequest
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.ClientService
import com.vitorbasso.gerenciadorinvestimentos.service.facade.ClientServiceFacadeImpl
import com.vitorbasso.gerenciadorinvestimentos.util.SecurityContextUtil
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFieldsExcept
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import utils.EasyRandomWrapper.random

class ClientServiceFacadeImplTest : StringSpec() {

    private val clientService = mockk<ClientService>()
    private val service = ClientServiceFacadeImpl(clientService)

    init {
        "should get client" {
            mockkObject(SecurityContextUtil)
            val client = random<Client>()
            every { SecurityContextUtil.getClientDetails() } returns client
            service.getClient() shouldBe client
            verify(exactly = 1) { SecurityContextUtil.getClientDetails() }
        }

        "should save client" {
            val clientToSave = random<Client>()
            every { clientService.saveClient(any()) } answers { firstArg() }
            service.saveClient(clientToSave) shouldBe clientToSave
            verify(exactly = 1) { clientService.saveClient(clientToSave) }
        }

        "should update client" {
            mockkObject(SecurityContextUtil)
            val client = random<Client>()
            val updateRequest = random<ClientUpdateRequest>()
            every { SecurityContextUtil.getClientDetails() } returns client
            every { clientService.updateClient(any(), any()) } answers {
                firstArg<Client>().copy(
                    name = secondArg<ClientUpdateRequest>().name ?: "wrong"
                )
            }
            val result = service.updateClient(updateRequest)
            result.shouldBeEqualToComparingFieldsExcept(client, Client::name, Client::dateUpdated, Client::dateCreated)
            result.name shouldBe updateRequest.name
        }

        "should delete client" {
            mockkObject(SecurityContextUtil)
            val client = random<Client>()
            every { SecurityContextUtil.getClientDetails() } returns client
            every { clientService.deleteClient(any()) } just runs
            service.deleteClient()
            val slot = slot<Client>()
            verify(exactly = 1) { clientService.deleteClient(capture(slot)) }
            slot.captured shouldBe client
        }
    }

}