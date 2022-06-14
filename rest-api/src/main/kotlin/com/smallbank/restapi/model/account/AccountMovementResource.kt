package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountMovement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

interface AccountMovementResource {

    @ResponseBody
    @GetMapping("{accountId}/movements")
    fun findAll(@PathVariable accountId: String): List<AccountMovement>
}
