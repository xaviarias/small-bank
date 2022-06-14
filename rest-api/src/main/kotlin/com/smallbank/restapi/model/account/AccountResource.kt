package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigDecimal

@ResponseBody
@RequestMapping("/customers/{customerId}/accounts")
interface AccountResource {

    @PostMapping
    fun create(
        @PathVariable("customerId") customerId: String
    ): Account

    @GetMapping("{accountId}")
    fun findById(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String
    ): Account

    @GetMapping
    fun findByCustomer(
        @PathVariable("customerId") customerId: String
    ): List<Account>

    @PutMapping("{accountId}/deposit")
    fun deposit(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String,
        amount: BigDecimal
    )

    @PutMapping("{accountId}/withdraw")
    fun withdraw(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String,
        amount: BigDecimal
    )
}
