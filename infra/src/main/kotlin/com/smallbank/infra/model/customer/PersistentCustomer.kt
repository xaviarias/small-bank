package com.smallbank.infra.model.customer

import javax.persistence.*

@Entity
@Table(name = "customers")
data class PersistentCustomer(
    @Id
    val id: String?,
    @Embedded
    val name: PersistentPersonalName,
    @Embedded
    val address: PersistentPersonalAddress
)

@Embeddable
@Access(AccessType.FIELD)
data class PersistentPersonalName(
    val first: String,
    val last: String
)

@Embeddable
@Access(AccessType.FIELD)
data class PersistentPersonalAddress(
    val streetName: String,
    val streetNumber: String,
    val postCode: String,
    val city: String,
    val isoCountryCode: String
)
