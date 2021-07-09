package com.vitorbasso.gerenciadorinvestimentos.dto.request

import javax.validation.constraints.Size

data class ClientUpdateRequest(
    @field:Size(message = "error.request.body.fields.client.name", min = 1)
    val name: String?
)