package com.basso.gerenciadorinvestimentos.application.dto.request

import javax.validation.constraints.Size

class WalletUpdateRequest(
        @field:Size(message = "error.request.body.fields.wallet.name.not-blank", min = 1)
        val name: String?,
        @field:Size(message = "error.request.body.fields.wallet.broker.not-blank", min = 1)
        val broker: String?
)