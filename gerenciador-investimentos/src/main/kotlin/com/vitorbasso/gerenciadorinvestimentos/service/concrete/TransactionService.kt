package com.vitorbasso.gerenciadorinvestimentos.service.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Asset
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomEntityNotFoundException
import com.vitorbasso.gerenciadorinvestimentos.repository.ITransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
internal class TransactionService(
    val transactionRepository: ITransactionRepository
) {

    fun getTransaction(transactionId: Long, clientId: Long) =
        this.transactionRepository.findByIdOrNull(transactionId)?.takeIf { it.asset.wallet.client.id == clientId }
            ?: throw CustomEntityNotFoundException(ManagerErrorCode.MANAGER_03)

    fun save(transaction: Transaction) = this.transactionRepository.save(transaction)

    fun saveAll(transactions: List<Transaction>): List<Transaction> = this.transactionRepository.saveAll(transactions)

    fun findAllByAsset(asset: Asset) = this.transactionRepository.findAllByAssetOrderByTransactionDate(asset)

    fun findAllByTicker(client: Client, ticker: String) =
        this.transactionRepository.findAllByAssetWalletClientAndAssetStockTickerOrderByTransactionDate(client, ticker)

    fun deleteTransaction(transaction: Transaction) = this.transactionRepository.delete(transaction)

}