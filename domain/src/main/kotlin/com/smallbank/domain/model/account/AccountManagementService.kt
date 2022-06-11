package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal

/**
 * Defines the port for account management.
 */
interface AccountManagementService {
    fun create(customerId: CustomerId): Account
    fun deposit(customerId: CustomerId, amount: BigDecimal)
    fun withdraw(customerId: CustomerId, amount: BigDecimal)
    fun balance(customerId: CustomerId): BigDecimal
    fun movements(customerId: CustomerId): List<AccountMovement>
}
