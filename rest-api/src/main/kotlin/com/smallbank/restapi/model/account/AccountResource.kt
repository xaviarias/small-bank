package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.customer.CustomerId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
interface AccountResource {

    @PostMapping("accounts")
    fun create(@RequestBody customerId: CustomerId): Account

    @GetMapping("accounts")
    fun findByCustomer(@RequestParam("customerId") customerId: String): List<Account>
}
