package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal
import java.util.*

data class Account(
    val accountId: AccountId,
    val customerId: CustomerId,
    val balance: BigDecimal
)

data class AccountId(val id: String) {
    companion object {
        fun create() = AccountId(UUID.randomUUID().toString())
    }
}
