package com.smallbank.domain.model.customer

import org.springframework.validation.annotation.Validated
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator

/**
 * Customer management business logic.
 */
@Validated
open class CustomerManagementServiceImpl(
    private val customerRepository: CustomerRepository,
    private val validator: Validator
) : CustomerManagementService {

    override fun create(@Valid customer: Customer): Customer {
        customer.validateOrThrow(validator)
        return customerRepository.create(customer)
    }

    override fun update(customer: Customer): Customer {
        customer.validateOrThrow(validator)
        return customerRepository.update(customer)
    }

    override fun findById(customerId: CustomerId): Customer? {
        return customerRepository.findById(customerId)
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
