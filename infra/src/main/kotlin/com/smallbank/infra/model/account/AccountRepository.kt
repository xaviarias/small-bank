package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.customer.CustomerId

internal interface AccountRepository {
    fun create(account: Account): Account
    fun update(account: Account): Account
    fun findById(accountId: AccountId): Account?
    fun findByCustomer(customerId: CustomerId): List<Account>
    fun movements(accountId: AccountId): List<AccountMovement>
}
