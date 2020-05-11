package com.basso.gerenciadorinvestimentos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class GerenciadorInvestimentosApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<GerenciadorInvestimentosApplication>(*args)
}
