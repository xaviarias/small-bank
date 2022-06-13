package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.AccountMovement
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "movements")
internal data class PersistentAccountMovement(

    @Id
    val id: String,

    val timestamp: LocalDateTime,

    @ManyToOne
    val account: PersistentAccount,

    @Enumerated(EnumType.STRING)
    val type: AccountMovement.MovementType,

    val amount: BigDecimal
)
