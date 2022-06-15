package com.smallbank.infra.model.customer

import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.CustomerRepository
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID

@DataJpaTest
open class CustomerRepositoryTest {

    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var jpaCustomerRepository: JpaCustomerRepository

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

    @BeforeEach
    fun setUp() {
        customerRepository = CustomerRepositoryImpl(jpaCustomerRepository)
    }

    @Test
    fun create() {
        val newCustomer = customerRepository.create(customer)
        Assertions.assertEquals(customer, newCustomer)
    }

    @Test
    fun update() {
        customerRepository.create(customer)

        val customerId = CustomerId(UUID.randomUUID().toString())
        val updatedCustomer = customerRepository.update(customer.copy(id = customerId))

        Assertions.assertEquals(customer.copy(id = customerId), updatedCustomer)
    }
}
