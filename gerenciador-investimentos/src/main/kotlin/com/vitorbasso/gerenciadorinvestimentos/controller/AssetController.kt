package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/\${api-version}/assets")
class AssetController(
    private val assetService: IAssetService
) {

    @GetMapping("/{ticker}")
    fun getAsset(@PathVariable ticker: String) = this.assetService.getAsset(ticker)

    @DeleteMapping("/{ticker}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAsset(
        @PathVariable ticker: String
    ) = this.assetService.deleteAsset(ticker)

}