package com.smallbank.infra.model.customer

import com.smallbank.infra.model.account.PersistentAccount
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "customers")
internal data class PersistentCustomer(
    @Id
    val id: String?,

    val email: String,

    @Embedded
    val name: PersistentPersonalName,

    @Embedded
    val address: PersistentPersonalAddress,

    @OneToMany(mappedBy = "customer")
    val accounts: List<PersistentAccount>
)

@Embeddable
@Access(AccessType.FIELD)
internal data class PersistentPersonalName(
    val first: String,
    val last: String
)

@Embeddable
@Access(AccessType.FIELD)
internal data class PersistentPersonalAddress(
    val streetName: String,
    val streetNumber: String,
    val postCode: String,
    val city: String,
    val isoCountryCode: String
)
