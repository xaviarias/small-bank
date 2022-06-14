package com.smallbank.restapi.model.customer

import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.CustomerManagementService
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerResourceImpl(
    private val customerService: CustomerManagementService
) : CustomerResource {

    override fun create(customer: Customer): Customer {
        return customerService.create(customer)
    }

    override fun findAll(): List<Customer> {
        return customerService.findAll()
    }

    override fun findById(id: String): Customer {
        return customerService.findById(CustomerId(id))
            ?: throw NoSuchElementException("id")
    }
}
