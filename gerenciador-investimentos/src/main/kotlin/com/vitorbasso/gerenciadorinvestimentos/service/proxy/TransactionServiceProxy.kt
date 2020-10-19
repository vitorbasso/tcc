package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.TransactionDto
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class TransactionServiceProxy (
    @Qualifier("transactionServiceFacadeImpl")
    private val transactionService: ITransactionService
) : ITransactionService {

    override fun performTransaction(transactionRequest: TransactionRequest)
        = this.transactionService.performTransaction(transactionRequest).getDto()

}

private fun ITransaction.getDto() = TransactionDto(
    type = (this as Transaction).type.name,
    quantity = this.quantity,
    value = this.value,
    ticker = this.asset.stock.ticker
)