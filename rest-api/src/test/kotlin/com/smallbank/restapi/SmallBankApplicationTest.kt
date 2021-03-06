package com.smallbank.restapi

import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.restapi.model.account.AccountAmountDto
import com.smallbank.restapi.model.account.AccountDto
import com.smallbank.restapi.model.customer.CustomerDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.web3j.crypto.Credentials
import java.util.UUID

@SpringBootTest(
    classes = [
        SmallBankApplication::class,
        SmallBankApplicationTestConfiguration::class
    ],
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
@ActiveProfiles("integration-test")
class SmallBankApplicationTest {

    @Value("\${smallbank.ethereum.account}")
    private var ethereumAccount: String? = null

    @Value("\${smallbank.ethereum.private-key}")
    private var privateKey: String? = null

    @Autowired
    private lateinit var keyVault: EthereumKeyVault

    @Autowired
    private lateinit var template: TestRestTemplate

    private val customer = CustomerDto(
        UUID.randomUUID().toString(),
        "john.doe@smail.com",
        "John",
        "Doe",
        "Baker Street",
        "221B",
        "NW1",
        "London",
        "GB"

    )

    private val account = AccountDto(
        "0x0",
        customer.id,
        AccountType.ETHEREUM
    )

    @BeforeAll
    fun setUp() {
        // Start with the Ethereum account key pair
        keyVault.store(ethereumAccount!!, Credentials.create(privateKey!!))
    }

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
            "/customers", customer, CustomerDto::class.java
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
            customer.id
        )
        assertTrue(accounts.body!!.isEmpty())
    }

    @Test
    @Order(5)
    fun `create account`() {
        val newAccount = template.postForEntity(
            "/customers/{customerId}/accounts",
            null, AccountDto::class.java,
            customer.id
        ).body!!
        assertEquals(account.copy(id = newAccount.id), newAccount)
    }

    @Test
    @Order(6)
    fun `list one account`() {
        val accounts = template.exchange(
            "/customers/{customerId}/accounts",
            HttpMethod.GET, null,
            ACCOUNT_LIST,
            customer.id
        ).body!!
        with(accounts) {
            assertTrue(size == 1)
            with(first().id) {
                assertTrue(contains(account.copy(id = this)))
            }
        }
    }

    @Test
    @Order(7)
    fun deposit() {
        val account = template.exchange(
            "/customers/{customerId}/accounts",
            HttpMethod.GET, null,
            ACCOUNT_LIST,
            customer.id
        ).body!!.first()
        template.put(
            "/customers/{customerId}/accounts/{accountId}/deposit",
            AccountAmountDto(1.toBigDecimal()),
            customer.id,
            account.id
        )
    }

    @Test
    @Order(8)
    fun withdraw() {
        val account = template.exchange(
            "/customers/{customerId}/accounts",
            HttpMethod.GET, null,
            ACCOUNT_LIST,
            customer.id
        ).body!!.first()
        template.put(
            "/customers/{customerId}/accounts/{accountId}/withdraw",
            AccountAmountDto(1.toBigDecimal()),
            customer.id,
            account.id
        )
    }

    companion object {
        private val CUSTOMER_LIST = object : ParameterizedTypeReference<List<CustomerDto>>() {}
        private val ACCOUNT_LIST = object : ParameterizedTypeReference<List<AccountDto>>() {}
    }
}
