package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountMovementResourceImpl(
    private val accountService: AccountManagementService
) : AccountMovementResource {

    override fun findAll(
        customerId: String,
        accountId: String
    ): List<AccountMovement> {
        return accountService.movements(AccountId(accountId))
    }
}
