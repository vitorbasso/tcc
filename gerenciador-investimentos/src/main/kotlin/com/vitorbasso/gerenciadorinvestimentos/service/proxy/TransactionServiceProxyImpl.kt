package com.vitorbasso.gerenciadorinvestimentos.service.proxy

import com.vitorbasso.gerenciadorinvestimentos.domain.ITransaction
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.dto.request.TransactionRequest
import com.vitorbasso.gerenciadorinvestimentos.dto.response.TransactionDto
import com.vitorbasso.gerenciadorinvestimentos.service.ITransactionService
import com.vitorbasso.gerenciadorinvestimentos.util.setScale
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class TransactionServiceProxyImpl(
    @Qualifier("transactionServiceFacadeImpl")
    private val transactionService: ITransactionService
) : ITransactionService {

    override fun performTransaction(transactionRequest: TransactionRequest) =
        this.transactionService.performTransaction(transactionRequest).getDto()

    override fun deleteTransaction(transactionId: Long) = this.transactionService.deleteTransaction(transactionId)

}

private fun ITransaction.getDto() = TransactionDto(
    id = (this as Transaction).id,
    type = this.type,
    quantity = this.quantity,
    value = this.value.setScale(),
    transactionDate = this.transactionDate,
)