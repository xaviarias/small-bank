package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.customer.CustomerId
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class AccountResourceImpl(
    private val accountService: AccountManagementService
) : AccountResource {

    override fun create(customerId: String): Account {
        return accountService.create(CustomerId(customerId))
    }

    override fun findById(customerId: String, accountId: String): Account {
        return accountService.find(AccountId(accountId))
    }

    override fun findByCustomer(customerId: String): List<Account> {
        return accountService.findByCustomer(CustomerId(customerId))
    }

    override fun deposit(customerId: String, accountId: String, amount: BigDecimal) {
        return accountService.deposit(AccountId(accountId), amount)
    }

    override fun withdraw(customerId: String, accountId: String, amount: BigDecimal) {
        return accountService.withdraw(AccountId(accountId), amount)
    }
}
