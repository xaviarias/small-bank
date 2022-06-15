package com.smallbank.domain.model.customer

/**
 * Customer management application service.
 */
interface CustomerManagementService {
    fun create(customer: Customer): Customer

    fun update(customer: Customer): Customer

    fun findById(customerId: CustomerId): Customer?

    // TODO Support pagination
    fun findAll(): List<Customer>
}
