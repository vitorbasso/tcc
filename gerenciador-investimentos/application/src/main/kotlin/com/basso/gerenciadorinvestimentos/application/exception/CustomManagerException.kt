package com.basso.gerenciadorinvestimentos.application.exception

import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode

open class CustomManagerException (
        val code: String = ManagerErrorCode.MANAGER_00.name,
        override val message: String = ManagerErrorCode.MANAGER_00.cause
) : RuntimeException()