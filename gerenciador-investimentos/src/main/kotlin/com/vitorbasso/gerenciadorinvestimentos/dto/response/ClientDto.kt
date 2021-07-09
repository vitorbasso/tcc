package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IClient

data class ClientDto(
    val id: Long,
    val name: String,
    val email: String
) : IClient