package com.smallbank.domain.model.customer

import java.security.Principal
import javax.validation.constraints.Email

data class Customer(
    val id: CustomerId,
    @Email val email: String,
    val name: PersonalName,
    val address: PersonalAddress
) : Principal {
    override fun getName() = id.id
}

data class CustomerId(val id: String)

data class PersonalName(val first: String, val last: String)

data class PersonalAddress(
    val streetName: String,
    val streetNumber: String,
    val postCode: String,
    val city: String,
    val isoCountryCode: String
)
