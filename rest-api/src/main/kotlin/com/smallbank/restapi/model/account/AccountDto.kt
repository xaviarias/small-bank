package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.customer.CustomerId
import java.math.BigDecimal

data class AccountDto(
    val id: String,
    val customerId: String,
    val type: AccountType
)

data class AccountTransferDto(
    val toAccount: String,
    val amount: BigDecimal
)

data class AccountBalanceDto(
    val balance: BigDecimal
)

internal fun AccountDto.toDomain() = Account(
    AccountId(id),
    CustomerId(customerId),
    type
)

internal fun Account.toDto() = AccountDto(
    id.id,
    customerId.id,
    type
)
