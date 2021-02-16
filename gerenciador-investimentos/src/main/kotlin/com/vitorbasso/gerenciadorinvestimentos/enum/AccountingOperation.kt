package com.vitorbasso.gerenciadorinvestimentos.enum

enum class AccountingOperation (
    val multiplier: Int
    ) {
    ADD_TRANSACTION(1),
    REMOVE_TRANSACTION(-1),
    REMOVE_ASSET(-1)
}