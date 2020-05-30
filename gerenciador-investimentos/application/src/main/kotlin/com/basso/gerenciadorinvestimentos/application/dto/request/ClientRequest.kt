package com.basso.gerenciadorinvestimentos.application.dto.request

import com.basso.gerenciadorinvestimentos.domain.IClient
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ClientRequest(

        @field:NotBlank(message = "error.request.body.fields.email.not-blank")
        @field:Email(message = "error.request.body.fields.email.format")
        val email: String,
        @field:NotBlank(message = "error.request.body.fields.password.not-blank")
        @field:Size(message = "error.request.body.fields.password.size", min = 8, max = 30)
        val password: String,
        @field:CPF(message = "error.request.body.fields.cpf")
        val cpf: String,
        @field:NotBlank(message = "error.request.body.fields.first-name")
        val firstName: String,
        @field:Size(message = "error.request.body.fields.last-name", min = 1)
        val lastName: String?,

        val avatarImage: String?
) : IClient