package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ClientRequest(

        @field:NotBlank(message = "error.request.body.fields.client.email.format")
        @field:Email(message = "error.request.body.fields.client.email.format")
        val email: String,
        @field:NotBlank(message = "error.request.body.fields.password.not-blank")
        @field:Size(message = "error.request.body.fields.password.size", min = 8, max = 30)
        val password: String,
        @field:CPF(message = "error.request.body.fields.client.cpf")
        val cpf: String,
        @field:NotBlank(message = "error.request.body.fields.client.first-name")
        val firstName: String,
        @field:Size(message = "error.request.body.fields.client.last-name", min = 1)
        val lastName: String?,

        @field:Size(message = "error.request.body.fields.client.avatar-image", min = 1)
        val avatarImage: String?
) : IClient