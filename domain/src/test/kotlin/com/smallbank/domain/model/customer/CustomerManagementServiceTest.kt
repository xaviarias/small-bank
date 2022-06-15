package com.smallbank.domain.model.customer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import java.util.UUID
import javax.validation.Validator

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
    private lateinit var repositoryMock: CustomerRepository
    private lateinit var validatorMock: Validator

    @BeforeEach
    fun setUp() {
        repositoryMock = mock {}
        validatorMock = mock {}
        service = CustomerManagementServiceImpl(repositoryMock, validatorMock)
    }

    @Test
    fun `create should validate and save the new customer in the repository`() {
        validatorMock.stub { on { validate(customer) } doReturn emptySet() }

        service.create(customer)
        verify(repositoryMock).create(customer)
        verify(validatorMock).validate(customer)
    }

    @Test
    fun `create should validate and update the customer in the repository`() {
        validatorMock.stub { on { validate(customer) } doReturn emptySet() }

        service.update(customer)
        verify(repositoryMock).update(customer)
        verify(validatorMock).validate(customer)
    }

    @Test
    fun `find by id should return the customer from the repository`() {
        repositoryMock.stub {
            on { findById(customer.id) } doReturn customer
        }

        val returnCustomer = service.findById(customer.id)
        Assertions.assertEquals(customer, returnCustomer)
        verify(repositoryMock).findById(customer.id)
    }

    @Test
    fun `find all should return all customers from the repository`() {
        repositoryMock.stub {
            on { findAll() } doReturn listOf(customer)
        }

        val allCustomers = service.findAll()
        Assertions.assertEquals(listOf(customer), allCustomers)
        verify(repositoryMock).findAll()
    }
}
