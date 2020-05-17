package com.basso.gerenciadorinvestimentos.application.handler

import com.basso.gerenciadorinvestimentos.application.dto.ApiErrorDto
import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode
import com.basso.gerenciadorinvestimentos.application.exception.CustomBadRequestException
import com.basso.gerenciadorinvestimentos.application.exception.CustomEntityNotFoundException
import com.basso.gerenciadorinvestimentos.application.exception.CustomManagerException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionAdvice (
        private val messageSource: MessageSource
) {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class, CustomManagerException::class)
    fun exceptionHandler(ex: Exception)
            = ApiErrorDto(
            ex = ex,
            errorEnum = ManagerErrorCode.MANAGER_00,
            getMessage = ::getLocalizedMessage
    )

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomEntityNotFoundException::class)
    fun entityNotFoundExceptionHandler(ex: CustomEntityNotFoundException)
            = ApiErrorDto(ex, ::getLocalizedMessage)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
            MethodArgumentNotValidException::class,
            CustomBadRequestException::class,
            HttpMessageNotReadableException::class
    )
    fun badRequestHandle(ex: Exception) = when(ex) {
        is CustomBadRequestException -> ApiErrorDto(ex, ::getLocalizedMessage)
        is MethodArgumentNotValidException -> ApiErrorDto(
                ex = ex,
                errorEnum = ManagerErrorCode.MANAGER_08,
                getMessage = ::getMethodArgumentNotValidExceptionMessage
        )
        else -> ApiErrorDto(ManagerErrorCode.MANAGER_01, ::getLocalizedMessage)
    }

    private fun getMethodArgumentNotValidExceptionMessage(ex: MethodArgumentNotValidException)
            = getLocalizedMessage(ManagerErrorCode.MANAGER_02.cause) +
            getObjectErrorsMessage(ex.bindingResult.fieldErrors)

    private fun getObjectErrorsMessage(errors: List<FieldError>)
            = errors.fold(
                    "",
                    { message, error -> message + " ${error.field} -> ${getLocalizedMessage(error.defaultMessage)}" }
            )

    private fun getLocalizedMessage(cause: String?)
            = this.messageSource.getMessage(cause ?: "", null, LocaleContextHolder.getLocale())

}