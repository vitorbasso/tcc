package com.basso.gerenciadorinvestimentos.application.dto

import com.basso.gerenciadorinvestimentos.domain.IClient

data class ClientDto (
        val firstName: String,
        val lastName: String?,
        val email: String,
        val cpf: String,
        val avatarImage: String?
) : IClient