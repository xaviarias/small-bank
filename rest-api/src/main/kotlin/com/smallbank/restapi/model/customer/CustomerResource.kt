package com.smallbank.restapi.model.customer

import com.smallbank.domain.model.customer.Customer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@ResponseBody
@RequestMapping("/customers")
interface CustomerResource {

    @PostMapping
    fun create(@RequestBody customer: Customer): Customer

    @GetMapping
    fun findAll(): List<Customer>

    @GetMapping("{id}")
    fun findById(@PathVariable id: String): Customer
}
