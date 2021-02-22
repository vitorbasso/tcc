package com.vitorbasso.gerenciadorinvestimentos.dto.response

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import com.vitorbasso.gerenciadorinvestimentos.exception.CustomManagerException
import org.springframework.web.bind.MethodArgumentNotValidException

data class ApiErrorDto(val code: String, val cause: String) {
    constructor(ex: CustomManagerException, getMessage: (String) -> String) : this(ex.code, getMessage(ex.message))

    constructor(errorEnum: ManagerErrorCode, getMessage: (String) -> String)
        : this(errorEnum.name, getMessage(errorEnum.cause))

    constructor(
        ex: Exception,
        errorEnum: ManagerErrorCode,
        getMessage: (String) -> String
    ) : this(errorEnum.name, getMessage(ex.message ?: errorEnum.cause))

    constructor(
        ex: MethodArgumentNotValidException,
        errorEnum: ManagerErrorCode,
        getMessage: (MethodArgumentNotValidException) -> String
    ) : this(errorEnum.name, getMessage(ex))

}