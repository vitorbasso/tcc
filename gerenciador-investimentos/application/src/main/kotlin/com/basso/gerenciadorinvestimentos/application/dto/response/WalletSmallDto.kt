package com.basso.gerenciadorinvestimentos.application.dto.response

import com.basso.gerenciadorinvestimentos.domain.IWallet

data class WalletSmallDto (
        val name: String,
        val broker: String
) : IWallet