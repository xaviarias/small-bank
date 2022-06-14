package com.smallbank.domain.model.account

import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal

/**
 * Defines the port for account management.
 */
interface AccountManagementService {
    fun create(customerId: CustomerId): Account
    fun find(accountId: AccountId): Account
    fun list(customerId: CustomerId): List<Account>
    fun deposit(accountId: AccountId, amount: BigDecimal)
    fun withdraw(accountId: AccountId, amount: BigDecimal)
    fun balance(accountId: AccountId): BigDecimal
    fun movements(accountId: AccountId): List<AccountMovement>
}
