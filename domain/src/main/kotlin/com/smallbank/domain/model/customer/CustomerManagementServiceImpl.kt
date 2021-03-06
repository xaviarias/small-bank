package com.smallbank.domain.model.customer

import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

/**
 * Customer management business logic.
 */
open class CustomerManagementServiceImpl(
    private val customerRepository: CustomerRepository,
    private val validator: Validator
) : CustomerManagementService {

    override fun create(@Valid customer: Customer): Customer {
        require(customerRepository.findById(customer.id) == null) {
            "Customer with id ${customer.id} already exists!"
        }
        customer.validateOrThrow(validator)
        return customerRepository.create(customer)
    }

    override fun update(customer: Customer): Customer {
        customer.validateOrThrow(validator)
        return customerRepository.update(customer)
    }

    override fun findById(customerId: CustomerId): Customer {
        return customerRepository.findById(customerId)
            ?: throw NoSuchElementException("No customer found: $customerId")
    }

    override fun findAll(): List<Customer> {
        return customerRepository.findAll()
    }

    private fun Any.validateOrThrow(validator: Validator) {
        validator.validate(this).also {
            if (it.isNotEmpty()) throw ConstraintViolationException(it)
        }
    }
}
