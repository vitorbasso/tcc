package com.vitorbasso.gerenciadorinvestimentos.service

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Transaction
import com.vitorbasso.gerenciadorinvestimentos.service.concrete.AccountingService

interface IAccountingServiceSubscriber {

    fun processAccountantReport(
        transaction: Transaction,
        accountantReport: AccountingService.AccountantReport
    ): AccountingService.AccountantReport

}