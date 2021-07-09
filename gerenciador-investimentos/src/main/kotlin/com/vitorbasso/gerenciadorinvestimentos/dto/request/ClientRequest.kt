package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ClientRequest(

    @field:NotBlank(message = "error.request.body.fields.client.email.format")
    @field:Email(message = "error.request.body.fields.client.email.format")
    val email: String,
    @field:NotBlank(message = "error.request.body.fields.password.format")
    @field:Size(message = "error.request.body.fields.password.format", min = 8, max = 30)
    val password: String,
    @field:NotBlank(message = "error.request.body.fields.client.first-name")
    val name: String

) : IClient