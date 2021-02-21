package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/\${api-version}/taxes")
class TaxController(
    private val taxService: ITaxService
) {

    @GetMapping
    fun getTax(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) month: LocalDate?
    ) = this.taxService.getTax(month ?: LocalDate.now().withDayOfMonth(1))

}