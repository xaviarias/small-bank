package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.customer.CustomerId
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountResourceImpl(
    private val accountService: AccountManagementService
) : AccountResource {

    override fun create(customerId: String): AccountDto {
        return accountService.create(CustomerId(customerId)).toDto()
    }

    override fun findById(customerId: String, accountId: String): AccountDto {
        return accountService.findById(AccountId(accountId)).toDto()
    }

    override fun findByCustomer(customerId: String): List<AccountDto> {
        return accountService.findByCustomer(CustomerId(customerId)).map { it.toDto() }
    }

    override fun deposit(
        customerId: String,
        accountId: String,
        amount: AccountAmountDto
    ) {
        accountService.deposit(AccountId(accountId), amount.amount)
    }

    override fun withdraw(
        customerId: String,
        accountId: String,
        amount: AccountAmountDto
    ) {
        accountService.withdraw(AccountId(accountId), amount.amount)
    }

    override fun balance(
        customerId: String,
        accountId: String
    ): AccountAmountDto {
        return AccountAmountDto(accountService.balance(AccountId(accountId)))
    }

    override fun transfer(
        customerId: String,
        accountId: String,
        transferDto: AccountTransferDto
    ) {
        accountService.transfer(
            AccountId(accountId),
            AccountId(transferDto.toAccount),
            transferDto.amount
        )
    }
}
