package com.basso.gerenciadorinvestimentos.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.basso.gerenciadorinvestimentos"])
@EnableFeignClients("com.basso.gerenciadorinvestimentos.integration")
@EntityScan("com.basso.gerenciadorinvestimentos.domain")
@EnableJpaRepositories("com.basso.gerenciadorinvestimentos.repository")
class GerenciadorInvestimentosApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<GerenciadorInvestimentosApplication>(*args)
}