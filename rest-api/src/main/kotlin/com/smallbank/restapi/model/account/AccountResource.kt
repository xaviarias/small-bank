package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
interface AccountResource {

    @PostMapping("customers/{customerId}/accounts")
    fun create(@PathVariable("customerId") customerId: String): Account

    @GetMapping("customers/{customerId}/accounts/{accountId}")
    fun findById(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String
    ): Account

    @GetMapping("customers/{customerId}/accounts")
    fun findByCustomer(@PathVariable("customerId") customerId: String): List<Account>
}
