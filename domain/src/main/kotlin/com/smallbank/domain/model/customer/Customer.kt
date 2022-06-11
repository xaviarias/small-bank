package com.smallbank.domain.model.customer

data class Customer(
    val id: CustomerId?,
    val name: PersonalName,
    val address: PersonalAddress
)

data class CustomerId(val id: String)

data class PersonalName(val first: String, val last: String)

data class PersonalAddress(
    val streetName: String,
    val streetNumber: String,
    val postCode: String,
    val city: String,
    val isoCountryCode: String
)
