package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal

data class Account(
    val accountId: AccountId,
    val customerId: CustomerId,
    val balance: BigDecimal
)

/**
 * The account id that can be a number or any other representation (e.g. address).
 */
data class AccountId(val id: String)
