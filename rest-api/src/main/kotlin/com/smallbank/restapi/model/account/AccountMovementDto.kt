package com.smallbank.restapi.model.account

import com.smallbank.domain.model.account.AccountMovement
import java.math.BigDecimal
import java.time.LocalDateTime

data class AccountMovementDto(
    val id: String,
    val timestamp: LocalDateTime,
    val accountId: String,
    val type: AccountMovement.MovementType,
    val amount: BigDecimal
)

internal fun AccountMovement.toDto() = AccountMovementDto(
    id = id,
    timestamp = timestamp,
    accountId = accountId.id,
    type = type,
    amount = amount
)
