package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.dto.request.TaxDeductibleRequest
import com.vitorbasso.gerenciadorinvestimentos.service.ITaxService
import com.vitorbasso.gerenciadorinvestimentos.util.atStartOfMonth
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@CrossOrigin
@RequestMapping("/\${api-version}/taxes")
class TaxController(
    private val taxService: ITaxService
) {

    @GetMapping
    fun getTax(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) month: LocalDate?
    ) = this.taxService.getTax(month?.atStartOfMonth() ?: LocalDate.now().atStartOfMonth())

    @PostMapping
    fun saveTaxDeductible(@RequestBody taxDeductibleRequest: TaxDeductibleRequest) =
        this.taxService.deduct(taxDeductibleRequest)

}