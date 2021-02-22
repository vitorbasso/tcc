package com.vitorbasso.gerenciadorinvestimentos.exception

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode

open class CustomManagerException(
    val code: String = ManagerErrorCode.MANAGER_00.name,
    override val message: String = ManagerErrorCode.MANAGER_00.cause
) : RuntimeException()