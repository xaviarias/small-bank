package com.smallbank.restapi.model.account

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigDecimal
import javax.validation.Valid

@ResponseBody
@RequestMapping("/customers/{customerId}/accounts")
interface AccountResource {

    @PostMapping
    fun create(
        @PathVariable("customerId") customerId: String
    ): AccountDto

    @GetMapping("{accountId}")
    fun findById(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String
    ): AccountDto

    @GetMapping
    fun findByCustomer(
        @PathVariable("customerId") customerId: String
    ): List<AccountDto>

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

    @GetMapping("{accountId}/balance")
    fun balance(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String
    ): AccountBalanceDto

    @PutMapping("{accountId}/transfer")
    fun transfer(
        @PathVariable("customerId") customerId: String,
        @PathVariable("accountId") accountId: String,
        @Valid @RequestBody transferDto: AccountTransferDto
    )
}
