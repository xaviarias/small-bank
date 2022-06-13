package com.smallbank.domain.model.customer

/**
 * Defines the port for persistent storage.
 */
interface CustomerRepository {
    fun create(customer: Customer): Customer
    fun update(customer: Customer): Customer
    fun findById(customerId: CustomerId): Customer?
    fun findAll(): List<Customer>
}
