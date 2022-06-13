package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.SmallBankConfiguration
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.toWei
import com.smallbank.infra.ethereum.web3j.SmallBank
import org.junit.jupiter.api.Assertions
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
import java.math.BigInteger
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
    private lateinit var service: AccountManagementService

    @MockBean
    private lateinit var repository: AccountRepository

    @MockBean
    private lateinit var keyVault: EthereumKeyVault

    @MockBean
    private lateinit var contract: SmallBank

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
        val account = service.create(customer.id)
        val credentialsCaptor = argumentCaptor<Credentials>()

        verify(keyVault).store(credentialsCaptor.capture())
        verify(repository).create(account)
    }

    @Test
    fun `create more than one account per customer should throw an exception`() {
        val account = service.create(customer.id)
        val credentialsCaptor = argumentCaptor<Credentials>()

        verify(keyVault).store(credentialsCaptor.capture())
        verify(repository).create(account)

        repository.stub {
            on {
                findByCustomer(customer.id)
            } doReturn listOf(account)
        }
        assertThrows<IllegalStateException> {
            service.create(customer.id)
        }
    }

    @Test
    fun `deposit should make a smart contract deposit`() {
        val account = service.create(customer.id)

        repository.stub {
            on { findByCustomer(customer.id) } doReturn listOf(account)
            on { findById(account.id) } doReturn account
        }

        val depositCall = mock<RemoteFunctionCall<TransactionReceipt>> {}
        val amount = 1.toWei(Convert.Unit.ETHER)
        contract.stub {
            on { deposit(amount) } doReturn depositCall
        }

        // Deposit 1 ETH to the bank
        service.deposit(account.id, amount.toBigDecimal())

        verify(contract).deposit(amount)
        verify(depositCall).send()
    }

    @Test
    fun `withdraw should make a smart contract withdraw`() {
        val account = service.create(customer.id)

        repository.stub {
            on { findByCustomer(customer.id) } doReturn listOf(account)
            on { findById(account.id) } doReturn account
        }

        val withdrawCall = mock<RemoteFunctionCall<TransactionReceipt>> {}
        val amount = 1.toWei(Convert.Unit.ETHER)
        contract.stub {
            on { withdraw(amount) } doReturn withdrawCall
        }

        // Withdraw 1 ETH from the bank
        service.withdraw(account.id, amount.toBigDecimal())

        verify(contract).withdraw(amount)
        verify(withdrawCall).send()
    }

    @Test
    fun `balance should call the smart contract balance`() {
        val amount = 1.toWei(Convert.Unit.ETHER)
        val balanceCall = mock<RemoteFunctionCall<BigInteger>> {
            on { send() } doReturn amount
        }
        contract.stub {
            on { balance() } doReturn balanceCall
        }

        val balance = service.balance(AccountId("0x0"))
        Assertions.assertEquals(amount, balance.toBigInteger())

        verify(contract).balance()
        verify(balanceCall).send()
    }
}
