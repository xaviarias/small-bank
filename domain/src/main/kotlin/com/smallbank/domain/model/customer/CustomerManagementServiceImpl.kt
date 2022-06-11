package com.smallbank.domain.model.customer

internal class CustomerManagementServiceImpl(
    private val customerRepository: CustomerRepository
) : CustomerManagementService {

    override fun create(customer: Customer): Customer {
        return customerRepository.save(customer)
    }

    override fun findById(customerId: CustomerId): Customer {
        return customerRepository.findById(customerId)
    }

    override fun findAll(): List<Customer> {
        return customerRepository.findAll()
    }
}
