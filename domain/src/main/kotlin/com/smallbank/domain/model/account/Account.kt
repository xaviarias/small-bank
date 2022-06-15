package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Past
import javax.validation.constraints.PositiveOrZero

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

data class AccountId(
    @NotBlank
    val id: String
) {
    override fun toString(): String = id
}

data class AccountMovement(
    val id: String,
    @Past
    val timestamp: LocalDateTime,
    val accountId: AccountId,
    val type: MovementType,
    @PositiveOrZero
    val amount: BigDecimal
) {
    enum class MovementType {
        DEPOSIT, WITHDRAW
    }
}
