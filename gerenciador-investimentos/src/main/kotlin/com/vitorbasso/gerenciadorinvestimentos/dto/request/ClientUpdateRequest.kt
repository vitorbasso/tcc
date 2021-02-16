package com.vitorbasso.gerenciadorinvestimentos.dto.request

import javax.validation.constraints.Size

data class ClientUpdateRequest(
    @field:Size(message = "error.request.body.fields.client.first-name", min = 1)
    val firstName: String?,

    @field:Size(message = "error.request.body.fields.client.last-name", min = 1)
    val lastName: String?,

    @field:Size(message = "error.request.body.fields.client.avatar-image", min = 1)
    val avatarImage: String?
)