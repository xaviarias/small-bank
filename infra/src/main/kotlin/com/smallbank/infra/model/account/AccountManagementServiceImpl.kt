package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.account.AccountMovement.MovementType
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.web3j.SmallBank
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountDepositEventResponse
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountWithdrawalEventResponse
import com.smallbank.infra.model.customer.JpaCustomerRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import org.web3j.tx.ChainIdLong
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.Clock
import java.time.LocalDateTime

@Service
@Qualifier("ethereum")
internal class AccountManagementServiceImpl(

    @Value("\${smallbank.ethereum.account}")
    private var ethereumAccount: String? = null,

    @Value("\${smallbank.ethereum.chain-id:${ChainIdLong.NONE}}")
    private var chainId: Long? = null,

    @Value("\${smallbank.ethereum.contract.address:#{null}}")
    private var contractAddress: String? = null,

    private val accountRepository: JpaAccountRepository,
    private val customerRepository: JpaCustomerRepository,
    private val movementRepository: JpaAccountMovementRepository,
    private val keyVault: EthereumKeyVault,
    private val web3j: Web3j,
    private val gasProvider: ContractGasProvider,
    private val random: SecureRandom,
    private val clock: Clock
) : AccountManagementService {

    override fun create(customerId: CustomerId): Account {
        val customer = customerRepository.findById(customerId.id).get()

        // Ensure only one Ethereum account exists per customer
        if (customer.accounts.any { it.type == AccountType.ETHEREUM }) {
            throw IllegalStateException("Already existing account for customer: $customerId")
        }

        val ecKeyPair = Keys.createEcKeyPair(random)
        val wallet: WalletFile = Wallet.createLight("", ecKeyPair)

        keyVault.store(wallet.address, Credentials.create(ecKeyPair))

        val accountId = AccountId(wallet.address)
        val account = Account(accountId, customerId, AccountType.ETHEREUM).toEntity(customer)

        return accountRepository.save(account).toPojo().also {
            subscribeToMovements(account)
        }
    }

    override fun find(accountId: AccountId): Account {
        return accountRepository.findById(accountId.id).get().toPojo()
    }

    override fun findByCustomer(customerId: CustomerId): List<Account> {
        customerRepository.findById(customerId.id).get()
        return accountRepository.findByCustomerId(customerId.id).map { it.toPojo() }
    }

    override fun deposit(accountId: AccountId, amount: BigDecimal) {
        accountRepository.findById(accountId.id).get()

        val credentials = resolveCredentials(accountId.id)
        loadContract(credentials).deposit(amount.toBigInteger()).send()
    }

    override fun withdraw(accountId: AccountId, amount: BigDecimal) {
        accountRepository.findById(accountId.id).get()

        val credentials = resolveCredentials(accountId.id)
        loadContract(credentials).withdraw(amount.toBigInteger()).send()
    }

    override fun balance(accountId: AccountId): BigDecimal {
        accountRepository.findById(accountId.id).get()

        val credentials = resolveCredentials(accountId.id)
        return loadContract(credentials).balance().send().toBigDecimal()
    }

    // TODO Support date intervals
    override fun movements(accountId: AccountId): List<AccountMovement> {
        accountRepository.findById(accountId.id).get()
        return movementRepository.findByAccountId(accountId.id).map { it.toPojo() }
    }

    /**
     * FIXME Filter events by account id.
     * FIXME Sync database only with delta blocks.
     */
    private fun subscribeToMovements(account: PersistentAccount) {
        require(ethereumAccount != null) { "No Ethereum account defined!" }
        val credentials = resolveCredentials(ethereumAccount!!)

        loadContract(credentials).accountDepositEventFlowable(EARLIEST, LATEST).subscribe {
            movementRepository.save(it.toEntity(account, clock))
        }
        loadContract(credentials).accountWithdrawalEventFlowable(EARLIEST, LATEST).subscribe {
            movementRepository.save(it.toEntity(account, clock))
        }
    }

    private fun resolveCredentials(account: String): Credentials {
        return keyVault.resolve(account) ?: throw IllegalStateException(
            "Account credentials not found in key vault: $account"
        )
    }

    private fun transactionManager(credentials: Credentials): TransactionManager {
        return RawTransactionManager(web3j, credentials, chainId ?: ChainIdLong.NONE)
    }

    private fun deployContract(): String {
        val credentials = resolveCredentials(ethereumAccount!!)
        return SmallBank.deploy(
            web3j,
            transactionManager(credentials),
            gasProvider
        ).send().contractAddress
    }

    private fun loadContract(credentials: Credentials): SmallBank {
        if (contractAddress == null) {
            contractAddress = deployContract()
        }
        return SmallBank.load(
            contractAddress,
            web3j,
            transactionManager(credentials),
            gasProvider
        )
    }
}

internal fun AccountDepositEventResponse.toEntity(
    account: PersistentAccount,
    clock: Clock
) = PersistentAccountMovement(
    id = log.transactionHash,
    timestamp = LocalDateTime.now(clock),
    account = account,
    type = MovementType.DEPOSIT,
    amount = amount.toBigDecimal()
)

internal fun AccountWithdrawalEventResponse.toEntity(
    account: PersistentAccount,
    clock: Clock
) = PersistentAccountMovement(
    id = log.transactionHash,
    timestamp = LocalDateTime.now(clock),
    account = account,
    type = MovementType.WITHDRAW,
    amount = amount.toBigDecimal()
)
