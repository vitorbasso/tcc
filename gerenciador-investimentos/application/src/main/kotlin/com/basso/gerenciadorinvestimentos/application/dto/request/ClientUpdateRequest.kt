package com.basso.gerenciadorinvestimentos.application.dto.request

import javax.validation.constraints.Size

data class ClientUpdateRequest (
        @field:Size(message = "error.request.body.fields.first-name", min = 1)
        val firstName: String?,

        @field:Size(message = "error.request.body.fields.last-name", min = 1)
        val lastName: String?,

        @field:Size(message = "error.request.body.fields.avatar-image", min = 1)
        val avatarImage: String?
)