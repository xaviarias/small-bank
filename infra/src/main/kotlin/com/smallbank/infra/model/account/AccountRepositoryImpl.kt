package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.model.customer.JpaCustomerRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal open class AccountRepositoryImpl(
    private val delegate: JpaAccountRepository,
    private val customerRepository: JpaCustomerRepository,
    private val movementsRepository: JpaAccountMovementRepository
) : AccountRepository {

    override fun create(account: Account): Account {
        return update(account)
    }

    override fun update(account: Account): Account {
        val persistentAccount = account.toEntity(
            customerRepository, movementsRepository
        )
        return delegate.save(persistentAccount).toPojo()
    }

    override fun findById(accountId: AccountId): Account? {
        return delegate.findById(accountId.id).map { it.toPojo() }.orElse(null)
    }

    override fun findByCustomer(customerId: CustomerId): List<Account> {
        return delegate.findAll().map { it.toPojo() }
    }

    override fun movements(accountId: AccountId): List<AccountMovement> {
        return delegate.findById(accountId.id).map { account ->
            account.movements.map { it.toPojo() }
        }.orElse(null)
    }
}

internal interface JpaAccountRepository : JpaRepository<PersistentAccount, String> {
    fun findByCustomerId(customerId: String): List<PersistentAccount>
}

internal interface JpaAccountMovementRepository : JpaRepository<PersistentAccountMovement, String> {
    fun findByAccountId(accountId: String): List<PersistentAccountMovement>
}

internal fun Account.toEntity(
    customerRepository: JpaCustomerRepository,
    movementsRepository: JpaAccountMovementRepository
) = PersistentAccount(
    id = id.id,
    type = type,
    customer = customerRepository.findById(customerId.id).orElseThrow(),
    movements = movementsRepository.findByAccountId(id.id)
)

internal fun PersistentAccount.toPojo() = Account(
    id = AccountId(id),
    customerId = CustomerId(customer.id!!),
    type = type
)

internal fun AccountMovement.toEntity(
    accountRepository: JpaAccountRepository
) = PersistentAccountMovement(
    id = id,
    timestamp = timestamp,
    account = accountRepository.findById(accountId.id).orElseThrow(),
    type = type,
    amount = amount
)

internal fun PersistentAccountMovement.toPojo() = AccountMovement(
    id = id,
    timestamp = timestamp,
    accountId = AccountId(account.id),
    type = type,
    amount = amount
)