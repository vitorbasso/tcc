package com.vitorbasso.gerenciadorinvestimentos.filter.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.vitorbasso.gerenciadorinvestimentos.dto.response.ApiErrorDto
import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RestAuthenticationEntryPoint(
    private val messageSource: MessageSource
) : AuthenticationEntryPoint {

    private val contentType = "application/json"

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {

        val responseDto = ApiErrorDto(ManagerErrorCode.MANAGER_08) {
            this.messageSource.getMessage(it, null, LocaleContextHolder.getLocale())
        }

        response?.status = HttpStatus.FORBIDDEN.value()
        response?.contentType = contentType
        val out = response?.outputStream
        val mapper = ObjectMapper()
        mapper.writeValue(out, responseDto)
        out?.flush()
    }
}