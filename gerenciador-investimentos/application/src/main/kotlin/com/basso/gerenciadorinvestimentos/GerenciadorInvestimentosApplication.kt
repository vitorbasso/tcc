package com.basso.gerenciadorinvestimentos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.basso.gerenciadorinvestimentos"])
@EntityScan("com.basso.gerenciadorinvestimentos.domain")
@EnableJpaRepositories("com.basso.gerenciadorinvestimentos.repository")
@EnableFeignClients("com.basso.gerenciadorinvestimentos.integration")
class GerenciadorInvestimentosApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
	runApplication<GerenciadorInvestimentosApplication>(*args)
}
