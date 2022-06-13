package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.infra.model.customer.PersistentCustomer
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "accounts")
internal data class PersistentAccount(
    @Id
    val id: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val customer: PersistentCustomer,

    @Enumerated(EnumType.STRING)
    val type: Account.AccountType
)
