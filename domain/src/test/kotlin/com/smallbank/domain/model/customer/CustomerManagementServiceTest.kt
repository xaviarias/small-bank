package com.smallbank.domain.model.customer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import java.util.UUID

/**
 * TODO Exhaustive tests.
 */
class CustomerManagementServiceTest {

    private val customer = Customer(
        CustomerId(UUID.randomUUID().toString()),
        "john.doe@smail.com",
        PersonalName("John", "Doe"),
        PersonalAddress(
            "Baker Street",
            "221B",
            "NW1",
            "London",
            "GB"
        )
    )

    private lateinit var service: CustomerManagementService
    private lateinit var repository: CustomerRepository

    @BeforeEach
    fun setUp() {
        repository = mock {}
        service = CustomerManagementServiceImpl(repository)
    }

    @Test
    fun `create should save a new customer in the repository`() {
        service.create(customer)
        verify(repository).create(customer)
    }

    @Test
    fun `find by id should return the customer from the repository`() {
        repository.stub {
            on { findById(customer.id) } doReturn customer
        }

        val returnCustomer = service.findById(customer.id)
        Assertions.assertEquals(customer, returnCustomer)
        verify(repository).findById(customer.id)
    }

    @Test
    fun `find all should return all customers from the repository`() {
        repository.stub {
            on { findAll() } doReturn listOf(customer)
        }

        val allCustomers = service.findAll()
        Assertions.assertEquals(listOf(customer), allCustomers)
        verify(repository).findAll()
    }
}
