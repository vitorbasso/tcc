package com.vitorbasso.gerenciadorinvestimentos.controller

import com.vitorbasso.gerenciadorinvestimentos.service.IAssetService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/\${api-version}/assets")
class AssetController (
    private val assetService: IAssetService
){

    @DeleteMapping("/{broker}/{ticker}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAsset(
        @PathVariable broker: String,
        @PathVariable ticker: String
    )= this.assetService.deleteAsset(broker, ticker)

}