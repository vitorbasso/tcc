package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet

data class WalletSmallDto(
    val id: Long,
    val name: String,
    val broker: String
) : IWallet