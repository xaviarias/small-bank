package com.smallbank.restapi.model.customer

import com.smallbank.domain.model.customer.Customer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
interface CustomerResource {

    @PostMapping("customers")
    fun create(@RequestBody customer: Customer): Customer

    @GetMapping("customers")
    fun findAll(): List<Customer>

    @GetMapping("customers/{id}")
    fun findById(@PathVariable id: String): Customer
}
