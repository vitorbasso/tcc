package com.vitorbasso.gerenciadorinvestimentos.enum

enum class ManagerErrorCode(val cause: String) {
    MANAGER_00("error.unexpected"),
    MANAGER_01("error.json"),
    MANAGER_02("error.request.body.fields"),
    MANAGER_03("error.entity.not-found"),
    MANAGER_04("error.entity.duplicate"),
    MANAGER_05("error.bad-request"),
    MANAGER_06("error.client.wallet.update"),
    MANAGER_07("error.client.wallet.delete"),
    MANAGER_08("error.authentication.bad-credentials"),
    MANAGER_09("error.forbidden"),
    MANAGER_10("error.transaction.bad.date"),
    MANAGER_11("error.client.monthly-wallet.delete")
}