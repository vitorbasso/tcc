package com.basso.gerenciadorinvestimentos.application.dto.request

import com.basso.gerenciadorinvestimentos.domain.IWallet

data class WalletRequest (
        val name: String,
        val broker: String
) : IWallet