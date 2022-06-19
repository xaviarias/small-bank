package com.smallbank.domain.model.customer

import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class Customer(
    @Valid
    val id: CustomerId,
    @Email
    @NotBlank
    val email: String,
    @Valid
    val name: PersonalName,
    @Valid
    val address: PersonalAddress
)

data class CustomerId(
    @NotBlank
    val id: String
) {
    override fun toString(): String = id
}

data class PersonalName(
    @NotBlank
    val first: String,
    @NotBlank
    val last: String
)

data class PersonalAddress(
    @NotBlank
    val streetName: String,
    @NotBlank
    val streetNumber: String,
    @NotBlank
    val postCode: String,
    @NotBlank
    val city: String,
    @NotBlank
    val isoCountryCode: String
)
