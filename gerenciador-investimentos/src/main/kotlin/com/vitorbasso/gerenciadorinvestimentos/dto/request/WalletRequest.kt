package com.vitorbasso.gerenciadorinvestimentos.dto.request

import com.vitorbasso.gerenciadorinvestimentos.domain.IWallet
import javax.validation.constraints.NotBlank

data class WalletRequest (
        @field:NotBlank(message = "error.request.body.fields.wallet.name.not-blank")
        val name: String,
        @field:NotBlank(message = "error.request.body.fields.wallet.broker.not-blank")
        val broker: String
) : IWallet