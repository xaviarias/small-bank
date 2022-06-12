package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.SmallBankConfiguration
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.toWei
import com.smallbank.infra.ethereum.web3j.CUSTOMER_PRIVATE_KEY
import com.smallbank.infra.ethereum.web3j.SmallBank
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Convert
import java.util.UUID

@SpringBootTest(
    classes = [
        SmallBankConfiguration::class,
        EthereumAccountManagementService::class
    ],
    webEnvironment = WebEnvironment.NONE
)
@ActiveProfiles("test")
class EthereumAccountManagementServiceTest {

    @Autowired
    @Qualifier("ethereum")
    private lateinit var accountManagementService: AccountManagementService

    @MockBean
    private lateinit var accountRepository: JpaAccountRepository

    @MockBean
    private lateinit var keyVault: EthereumKeyVault

    @MockBean
    private lateinit var smallBank: SmallBank

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

    @Test
    fun `create account should save a new account and store the keys in the key vault`() {
        val account = accountManagementService.create(customer.id)
        val credentialsCaptor = argumentCaptor<Credentials>()

        verify(keyVault).store(credentialsCaptor.capture())
        verify(accountRepository).save(account)
    }

    @Test
    fun `create more than one account per customer should throw an exception`() {
        val account = accountManagementService.create(customer.id)
        val credentialsCaptor = argumentCaptor<Credentials>()

        verify(keyVault).store(credentialsCaptor.capture())
        verify(accountRepository).save(account)

        accountRepository.stub {
            on {
                findByCustomer(customer.id)
            } doReturn listOf(account)
        }
        assertThrows<IllegalStateException> {
            accountManagementService.create(customer.id)
        }
    }

    @Test
    fun `deposit should make an Ethereum call`() {
        val account = accountManagementService.create(customer.id)

        keyVault.stub {
            on {
                resolve(account.id.id)
            } doReturn Credentials.create(CUSTOMER_PRIVATE_KEY)
        }

        accountRepository.stub {
            on {
                findByCustomer(customer.id)
            } doReturn listOf(account)
        }

        val amount = 1.toWei(Convert.Unit.ETHER)
        val depositCall = mock<RemoteFunctionCall<TransactionReceipt>> {}

        smallBank.stub {
            on {
                deposit(amount)
            } doReturn depositCall
        }

        // Deposit 1 ETH to the bank
        accountManagementService.deposit(account.id, amount.toBigDecimal())

        verify(smallBank).deposit(amount)
        verify(depositCall).send()
    }
}
