package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.CustomerRepository
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.ethereum.web3j.SMALLBANK_ACCOUNT
import com.smallbank.infra.model.customer.CustomerRepositoryImpl
import com.smallbank.infra.model.customer.JpaCustomerRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID

@DataJpaTest
open class AccountRepositoryTest {

    private lateinit var accountRepository: AccountRepository
    private lateinit var customerRepository: CustomerRepository
    private lateinit var movementsRepository: JpaAccountMovementRepository

    @Autowired
    private lateinit var jpaAccountRepository: JpaAccountRepository

    @Autowired
    private lateinit var jpaCustomerRepository: JpaCustomerRepository

    private val customer = Customer(
        CustomerId(UUID.randomUUID().toString()),
        "john.doe@smail.com",
        PersonalName("John", "Doe"),
        PersonalAddress(
            "Baker Street",
            "221B",
            "NW1",
            "London",
            "GB"
        )
    )

    private val account = Account(
        id = AccountId(SMALLBANK_ACCOUNT),
        customerId = customer.id,
        type = Account.AccountType.ETHEREUM
    )

    @BeforeEach
    fun setUp() {
        accountRepository = AccountRepositoryImpl(
            jpaAccountRepository,
            jpaCustomerRepository
        )
        customerRepository = CustomerRepositoryImpl(jpaCustomerRepository)
    }

    @Test
    fun create() {
        customerRepository.create(customer)
        val account = accountRepository.create(account)
        Assertions.assertEquals(account, accountRepository.findById(account.id))
    }

    @Test
    fun update() {
        customerRepository.create(customer)
        var account = accountRepository.create(account)

        val customerId = CustomerId(UUID.randomUUID().toString())
        customerRepository.create(customer.copy(id = customerId))

        val newAccount = account.copy(customerId = customerId)
        account = accountRepository.update(newAccount)

        Assertions.assertEquals(newAccount, accountRepository.findById(account.id))
    }
}
