package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.model.customer.JpaCustomerRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
internal class AccountRepositoryImpl(
    private val delegate: JpaAccountRepository,
    private val customerRepository: JpaCustomerRepository
) : AccountRepository {

    override fun create(account: Account): Account {
        return update(account)
    }

    override fun update(account: Account): Account {
        return delegate.save(account.toEntity(customerRepository)).toPojo()
    }

    override fun findById(accountId: AccountId): Account? {
        return delegate.findById(accountId.id).map { it.toPojo() }.orElse(null)
    }

    override fun findByCustomer(customerId: CustomerId): List<Account> {
        return delegate.findAll().map { it.toPojo() }
    }
}

internal interface JpaAccountRepository : JpaRepository<PersistentAccount, String> {
    fun findByCustomerId(customerId: String): List<PersistentAccount>
}

internal fun Account.toEntity(
    customerRepository: JpaCustomerRepository
) = PersistentAccount(
    id = id.id,
    customer = customerRepository.findById(customerId.id).orElseThrow(),
    type = type
)

internal fun PersistentAccount.toPojo() = Account(
    id = AccountId(id),
    customerId = CustomerId(customer.id!!),
    type = type
)