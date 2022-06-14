package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.customer.CustomerId
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountResourceImpl(
    private val accountService: AccountManagementService
) : AccountResource {

    override fun create(customerId: CustomerId): Account {
        return accountService.create(customerId)
    }

    override fun findByCustomer(customerId: String): List<Account> {
        return accountService.list(CustomerId(customerId))
    }
}
