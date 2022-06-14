package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountMovement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
@RequestMapping("/customers/{customerId}/accounts/{accountId}")
interface AccountMovementResource {

    @ResponseBody
    @GetMapping("movements")
    fun findAll(
        @PathVariable customerId: String,
        @PathVariable accountId: String
    ): List<AccountMovement>
}
