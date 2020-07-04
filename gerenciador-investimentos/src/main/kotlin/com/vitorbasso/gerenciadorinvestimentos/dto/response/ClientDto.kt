package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient

data class ClientDto (
        val firstName: String,
        val lastName: String?,
        val email: String,
        val cpf: String,
        val avatarImage: String?
) : IClient