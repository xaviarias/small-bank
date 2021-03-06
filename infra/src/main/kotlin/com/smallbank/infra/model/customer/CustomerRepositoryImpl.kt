package com.smallbank.infra.model.customer

import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.CustomerRepository
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal open class CustomerRepositoryImpl(
    private val delegate: JpaCustomerRepository
) : CustomerRepository {

    override fun create(customer: Customer): Customer {
        return delegate.save(customer.toEntity()).toPojo()
    }

    override fun update(customer: Customer): Customer {
        return delegate.save(customer.toEntity()).toPojo()
    }

    override fun findById(customerId: CustomerId): Customer? {
        return delegate.findById(customerId.id).map { it.toPojo() }.orElse(null)
    }

    override fun findAll(): List<Customer> {
        return delegate.findAll().map { it.toPojo() }
    }
}

internal interface JpaCustomerRepository : JpaRepository<PersistentCustomer, String>

internal fun Customer.toEntity() = PersistentCustomer(
    id = id.id,
    email = email,
    name = PersistentPersonalName(
        name.first,
        name.last
    ),
    address = PersistentPersonalAddress(
        address.streetName,
        address.streetNumber,
        address.postCode,
        address.city,
        address.isoCountryCode
    ),
    accounts = emptyList()
)

internal fun PersistentCustomer.toPojo() = Customer(
    id = CustomerId(id!!),
    email = email,
    name = PersonalName(
        name.first,
        name.last
    ),
    address = PersonalAddress(
        address.streetName,
        address.streetNumber,
        address.postCode,
        address.city,
        address.isoCountryCode
    )
)