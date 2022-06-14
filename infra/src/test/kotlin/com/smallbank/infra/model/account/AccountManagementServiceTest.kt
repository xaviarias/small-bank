package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement.MovementType
import com.smallbank.domain.model.customer.Customer
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.domain.model.customer.PersonalAddress
import com.smallbank.domain.model.customer.PersonalName
import com.smallbank.infra.config.SmallBankConfiguration
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.toWei
import com.smallbank.infra.ethereum.web3j.CONTRACT_ADDRESS
import com.smallbank.infra.ethereum.web3j.CUSTOMER_ACCOUNT
import com.smallbank.infra.ethereum.web3j.CUSTOMER_PRIVATE_KEY
import com.smallbank.infra.ethereum.web3j.SMALLBANK_ACCOUNT
import com.smallbank.infra.ethereum.web3j.SMALLBANK_PRIVATE_KEY
import com.smallbank.infra.ethereum.web3j.SmallBank
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountDepositEventResponse
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountWithdrawalEventResponse
import com.smallbank.infra.ethereum.web3j.SmallBank.deploy
import com.smallbank.infra.ethereum.web3j.SmallBank.load
import com.smallbank.infra.model.customer.JpaCustomerRepository
import com.smallbank.infra.model.customer.toEntity
import io.reactivex.Flowable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import org.web3j.protocol.core.RemoteFunctionCall
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Optional
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest(
    classes = [SmallBankConfiguration::class],
    webEnvironment = WebEnvironment.NONE
)
@ActiveProfiles("test")
class AccountManagementServiceTest {

    @Autowired
    @Qualifier("ethereum")
    private lateinit var service: AccountManagementService

    @Autowired
    private lateinit var keyVault: EthereumKeyVault

    @Autowired
    private lateinit var web3j: Web3j

    @Autowired
    private lateinit var gasProvider: ContractGasProvider

    @Autowired
    private lateinit var clock: Clock

    @MockBean
    private lateinit var accountRepositoryMock: JpaAccountRepository

    @MockBean
    private lateinit var customerRepositoryMock: JpaCustomerRepository

    @MockBean
    private lateinit var movementsRepositoryMock: JpaAccountMovementRepository

    @MockBean
    private lateinit var contractMock: SmallBank

    private lateinit var staticContractMock: MockedStatic<SmallBank>

    @Configuration
    open class TestConfiguration {
        @Bean
        @Primary
        open fun testClock(): Clock = Clock.fixed(Instant.now(), ZoneOffset.UTC)
    }

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
        AccountId(CUSTOMER_ACCOUNT),
        customer.id,
        AccountType.ETHEREUM
    )

    @BeforeEach
    fun setUp() {
        // Start with the Ethereum account key pair
        val credentials = Credentials.create(SMALLBANK_PRIVATE_KEY)
        keyVault.store(SMALLBANK_ACCOUNT, credentials)

        // Stub contract deployment and loading
        val deployCall = mock<RemoteFunctionCall<SmallBank>>()
        deployCall.stub { on { send() } doReturn contractMock }

        staticContractMock = mockStatic(SmallBank::class.java).apply {
            whenever(deploy(web3j, credentials, gasProvider)) doReturn deployCall
            whenever(load(CONTRACT_ADDRESS, web3j, credentials, gasProvider)) doReturn contractMock
        }

        // Stub contract methods
        contractMock.stub {
            on { contractAddress } doReturn CONTRACT_ADDRESS
        }
    }

    @AfterEach
    fun tearDown() {
        reset(
            accountRepositoryMock,
            customerRepositoryMock,
            movementsRepositoryMock,
            contractMock
        )
        staticContractMock.close()
    }

    @Test
    fun `create account should save a new account in the repository, store its credentials in the key vault and subscribe to events`() {
        val persistentCustomer = customer.toEntity()
        val persistentAccount = AtomicReference<PersistentAccount>()

        // Stub repositories
        accountRepositoryMock.stub {
            onGeneric { save(any()) } doAnswer {
                val accountId = (it.arguments[0] as PersistentAccount).id
                with(persistentAccount) {
                    set(account.toEntity(persistentCustomer).copy(id = accountId))
                    get()
                }
            }
        }
        customerRepositoryMock.stub {
            on {
                findById(customer.id.id)
            } doReturn Optional.of(persistentCustomer)
        }

        // Stub contract emitted events
        val depositEvent = AccountDepositEventResponse().apply {
            account = CUSTOMER_ACCOUNT
            amount = 1.toWei(Convert.Unit.ETHER)
            log = Log().apply { transactionHash = "0x0" }
        }
        val withdrawEvent = AccountWithdrawalEventResponse().apply {
            account = CUSTOMER_ACCOUNT
            amount = 1.toWei(Convert.Unit.ETHER)
            log = Log().apply { transactionHash = "0x1" }
        }
        contractMock.stub {
            on {
                accountDepositEventFlowable(EARLIEST, LATEST)
            } doReturn Flowable.just(depositEvent)
            on {
                accountWithdrawalEventFlowable(EARLIEST, LATEST)
            } doReturn Flowable.just(withdrawEvent)
        }

        // Create new account
        val createdAccount = service.create(customer.id)

        // Verify repositories
        with(persistentAccount.get()) {
            assertEquals(toPojo(), createdAccount)
            verify(accountRepositoryMock).save(this)
            verify(movementsRepositoryMock).save(depositEvent.toEntity(this, clock))
            verify(movementsRepositoryMock).save(withdrawEvent.toEntity(this, clock))
        }

        // Verify key vault and contract events
        assertNotNull(keyVault.resolve(createdAccount.id.id))
        verify(contractMock).accountDepositEventFlowable(EARLIEST, LATEST)
        verify(contractMock).accountWithdrawalEventFlowable(EARLIEST, LATEST)
    }

    @Test
    fun `create more than one account per customer should throw an exception`() {
        val persistentCustomer = customer.toEntity().let {
            it.copy(accounts = listOf(account.toEntity(it)))
        }

        customerRepositoryMock.stub {
            on {
                findById(customer.id.id)
            } doReturn Optional.of(persistentCustomer)
        }
        assertThrows<IllegalStateException> {
            service.create(customer.id)
        }

        verify(customerRepositoryMock).findById(customer.id.id)
        verifyNoInteractions(accountRepositoryMock)
    }

    @Test
    fun `deposit should make a smart contract deposit`() {

        val depositCall = mock<RemoteFunctionCall<TransactionReceipt>> {}
        val amount = 1.toWei(Convert.Unit.ETHER)
        contractMock.stub {
            on { deposit(amount) } doReturn depositCall
        }

        accountRepositoryMock.stub {
            on {
                findById(CUSTOMER_ACCOUNT)
            } doReturn Optional.of(account.toEntity(customer.toEntity()))
        }
        keyVault.store(CUSTOMER_ACCOUNT, Credentials.create(CUSTOMER_PRIVATE_KEY))

        // Deposit 1 ETH to the bank (account is ignored)
        service.deposit(AccountId(CUSTOMER_ACCOUNT), amount.toBigDecimal())

        verify(accountRepositoryMock).findById(CUSTOMER_ACCOUNT)
        verify(contractMock).deposit(amount)
        verify(depositCall).send()
    }

    @Test
    fun `withdraw should make a smart contract withdraw`() {

        val withdrawCall = mock<RemoteFunctionCall<TransactionReceipt>> {}
        val amount = 1.toWei(Convert.Unit.ETHER)

        contractMock.stub {
            on { withdraw(amount) } doReturn withdrawCall
        }
        accountRepositoryMock.stub {
            on {
                findById(CUSTOMER_ACCOUNT)
            } doReturn Optional.of(account.toEntity(customer.toEntity()))
        }
        keyVault.store(CUSTOMER_ACCOUNT, Credentials.create(CUSTOMER_PRIVATE_KEY))

        // Withdraw 1 ETH from the bank
        service.withdraw(AccountId(CUSTOMER_ACCOUNT), amount.toBigDecimal())

        verify(accountRepositoryMock).findById(CUSTOMER_ACCOUNT)
        verify(contractMock).withdraw(amount)
        verify(withdrawCall).send()
    }

    @Test
    fun `balance should call the smart contract balance`() {
        val amount = 1.toWei(Convert.Unit.ETHER)
        val balanceCall = mock<RemoteFunctionCall<BigInteger>> {
            on { send() } doReturn amount
        }
        contractMock.stub {
            on { balance() } doReturn balanceCall
        }

        accountRepositoryMock.stub {
            on {
                findById(CUSTOMER_ACCOUNT)
            } doReturn Optional.of(account.toEntity(customer.toEntity()))
        }
        keyVault.store(CUSTOMER_ACCOUNT, Credentials.create(CUSTOMER_PRIVATE_KEY))

        val balance = service.balance(AccountId(CUSTOMER_ACCOUNT))
        assertEquals(amount, balance.toBigInteger())

        verify(accountRepositoryMock).findById(CUSTOMER_ACCOUNT)
        verify(contractMock).balance()
        verify(balanceCall).send()
    }

    @Test
    fun `movements should list deposits and withdrawals`() {
        val persistentAccount = account.toEntity(customer.toEntity())

        accountRepositoryMock.stub {
            on { findById(CUSTOMER_ACCOUNT) } doReturn Optional.of(persistentAccount)
        }

        val deposit = PersistentAccountMovement(
            "0x0",
            LocalDateTime.now(),
            persistentAccount,
            MovementType.DEPOSIT,
            100000.toBigDecimal()
        )
        val withdrawal = PersistentAccountMovement(
            "0x1",
            LocalDateTime.now(),
            persistentAccount,
            MovementType.WITHDRAW,
            100000.toBigDecimal()
        )
        movementsRepositoryMock.stub {
            on { findByAccountId(CUSTOMER_ACCOUNT) } doReturn listOf(deposit, withdrawal)
        }

        keyVault.store(CUSTOMER_ACCOUNT, Credentials.create(CUSTOMER_PRIVATE_KEY))

        val movements = service.movements(account.id)
        assertEquals(listOf(deposit.toPojo(), withdrawal.toPojo()), movements)

        verify(accountRepositoryMock).findById(CUSTOMER_ACCOUNT)
        verify(movementsRepositoryMock).findByAccountId(account.id.id)
    }
}
