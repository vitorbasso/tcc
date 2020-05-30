package com.basso.gerenciadorinvestimentos.application.enum

enum class ManagerErrorCode (val cause: String) {
    MANAGER_00("error.unexpected"),
    MANAGER_01("error.json"),
    MANAGER_02("error.request.body.fields"),
    MANAGER_03("error.entity.not-found"),
    MANAGER_04("error.entity.duplicate"),
    MANAGER_05("error.client.update"),
    MANAGER_06("error.client.delete"),
    MANAGER_07("error.bad-request"),
    MANAGER_08("error.client.wallet.save"),
    MANAGER_09("error.client.wallet.update"),
    MANAGER_10("error.client.wallet.delete")
}