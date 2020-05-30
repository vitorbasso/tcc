package com.basso.gerenciadorinvestimentos.application.dto.request

import com.basso.gerenciadorinvestimentos.domain.IWallet
import javax.validation.constraints.NotBlank

data class WalletRequest (
        @field:NotBlank(message = "error.request.body.fields.wallet.name.not-blank")
        val name: String,
        @field:NotBlank(message = "error.request.body.fields.wallet.broker.not-blank")
        val broker: String
) : IWallet