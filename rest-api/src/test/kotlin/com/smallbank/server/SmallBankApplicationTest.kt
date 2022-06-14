package com.smallbank.server

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.web3j.SmallBank
import com.smallbank.restapi.SmallBankApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import java.util.UUID

@SpringBootTest(
    classes = [SmallBankApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
class SmallBankApplicationTest {

    @MockBean
    private lateinit var keyVault: EthereumKeyVault

    @MockBean
    private lateinit var contract: SmallBank

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

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
        AccountId("0x0"),
        customer.id,
        AccountType.ETHEREUM
    )

    @Test
    fun `list customers`() {
        val customers = restTemplate.getForEntity("/customers", List::class.java)
        assertTrue(customers.body!!.isEmpty())
    }

    @Test
    fun `create customer`() {
        val newCustomer = restTemplate.postForEntity(
            "/customers", customer, Customer::class.java
        )
        assertEquals(customer, newCustomer.body!!)
    }

    @Test
    fun `create account`() {
        val newAccount = restTemplate.postForEntity(
            "/accounts", account, Account::class.java
        )
        assertEquals(account, newAccount.body!!)
    }
}
