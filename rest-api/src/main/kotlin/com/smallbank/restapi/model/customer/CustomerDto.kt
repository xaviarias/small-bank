package com.smallbank.restapi.model.customer

import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName

data class CustomerDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val streetName: String,
    val streetNumber: String,
    val postCode: String,
    val city: String,
    val isoCountryCode: String
)

internal fun CustomerDto.toDomain() = Customer(
    CustomerId(id),
    email,
    PersonalName(
        firstName,
        lastName
    ),
    PersonalAddress(
        streetName,
        streetNumber,
        postCode,
        city,
        isoCountryCode
    )
)

internal fun Customer.toDto() = CustomerDto(
    id.id,
    email,
    name.first,
    name.last,
    address.streetName,
    address.streetNumber,
    address.postCode,
    address.city,
    address.isoCountryCode
)
