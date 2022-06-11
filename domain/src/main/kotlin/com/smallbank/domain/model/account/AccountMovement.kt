package com.smallbank.domain.model.account

import java.math.BigDecimal

data class AccountMovement(
    val type: MovementType,
    val amount: BigDecimal
) {
    enum class MovementType {
        DEPOSIT, WITHDRAW
    }
}
