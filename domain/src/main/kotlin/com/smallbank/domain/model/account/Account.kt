package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal

data class Account(
    val id: AccountId,
    val customerId: CustomerId,
    val type: AccountType
) {
    /**
     * Lists the possible account types supported by the system.
     */
    enum class AccountType {
        /**
         * Only one Ethereum account per customer should be ensured.
         */
        ETHEREUM
    }
}

data class AccountId(val id: String)

data class AccountMovement(
    val accountId: AccountId,
    val type: MovementType,
    val amount: BigDecimal
) {
    enum class MovementType {
        DEPOSIT, WITHDRAW
    }
}
