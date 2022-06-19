package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountMovementResourceImpl(
    private val accountService: AccountManagementService
) : AccountMovementResource {

    override fun findAll(
        customerId: String,
        accountId: String
    ): List<AccountMovementDto> {
        return accountService.movements(AccountId(accountId)).map { it.toDto() }
    }
}
