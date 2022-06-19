package com.smallbank.restapi.model.customer

import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.CustomerManagementService
import org.springframework.web.bind.annotation.RestController

@RestController
open class CustomerResourceImpl(
    private val customerService: CustomerManagementService
) : CustomerResource {

    override fun create(customer: CustomerDto): CustomerDto {
        return customerService.create(customer.toDomain()).toDto()
    }

    override fun findAll(): List<CustomerDto> {
        return customerService.findAll().map { it.toDto() }
    }

    override fun findById(id: String): CustomerDto {
        return customerService.findById(CustomerId(id)).toDto()
    }
}
