package com.smallbank.restapi.model.customer

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.validation.Valid

@Validated
@ResponseBody
@RequestMapping("/customers")
interface CustomerResource {

    @PostMapping
    fun create(@Valid @RequestBody customer: CustomerDto): CustomerDto

    @GetMapping
    fun findAll(): List<CustomerDto>

    @GetMapping("{id}")
    fun findById(@PathVariable id: String): CustomerDto
}
