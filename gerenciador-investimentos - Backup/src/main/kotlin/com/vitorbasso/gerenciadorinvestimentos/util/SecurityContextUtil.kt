package com.vitorbasso.gerenciadorinvestimentos.util

import com.vitorbasso.gerenciadorinvestimentos.domain.concrete.Client
import org.springframework.security.core.context.SecurityContextHolder

object SecurityContextUtil {

    fun getClientDetails() = SecurityContextHolder.getContext().authentication.principal as Client

}