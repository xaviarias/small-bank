package com.smallbank.server

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.restapi.SmallBankApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.web3j.EVMTest
import java.util.UUID

@EVMTest
@SpringBootTest(
    classes = [SmallBankApplication::class],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class SmallBankApplicationTest {

    @MockBean
    private lateinit var keyVault: EthereumKeyVault

    @Autowired
    private lateinit var template: TestRestTemplate

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
    @Order(1)
    fun `list no customers`() {
        val customers = template.getForEntity("/customers", List::class.java)
        assertTrue(customers.body!!.isEmpty())
    }

    @Test
    @Order(2)
    fun `create customer`() {
        val newCustomer = template.postForEntity(
            "/customers", customer, Customer::class.java
        )
        assertEquals(customer, newCustomer.body!!)
    }

    @Test
    @Order(3)
    fun `list customers contains the created customer`() {
        val customers = template.exchange(
            "/customers",
            HttpMethod.GET,
            null,
            CUSTOMER_LIST
        ).body!!
        assertTrue(customers.contains(customer))
    }

    @Test
    @Order(4)
    fun `list no accounts`() {
        val accounts = template.getForEntity(
            "/customers/{customerId}/accounts",
            List::class.java,
            customer.id.id
        )
        assertTrue(accounts.body!!.isEmpty())
    }

    @Test
    @Order(5)
    fun `create account`() {
        val newAccount = template.postForEntity(
            "/customers/{customerId}/accounts",
            null, Account::class.java,
            customer.id.id
        )
        assertEquals(account, newAccount.body!!)
    }

    @Test
    @Order(6)
    fun `list one account`() {
        val accounts = template.exchange(
            "/customers/{customerId}/accounts",
            HttpMethod.GET, null,
            ACCOUNT_LIST,
            customer.id.id
        ).body!!
        with(accounts) {
            assertTrue(size == 1)
            with(first().id) {
                assertTrue(contains(account.copy(id = this)))
            }
        }
    }

    companion object {
        private val CUSTOMER_LIST = object : ParameterizedTypeReference<List<Customer>>() {}
        private val ACCOUNT_LIST = object : ParameterizedTypeReference<List<Account>>() {}
    }
}
