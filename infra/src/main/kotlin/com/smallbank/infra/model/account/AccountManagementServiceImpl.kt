package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.account.AccountMovement.MovementType
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.toHexString
import com.smallbank.infra.ethereum.web3j.SmallBank
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountDepositEventResponse
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountWithdrawalEventResponse
import com.smallbank.infra.model.customer.JpaCustomerRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.protocol.core.DefaultBlockParameterName.EARLIEST
import org.web3j.protocol.core.DefaultBlockParameterName.LATEST
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.Clock
import java.time.LocalDateTime

@Service
@Qualifier("ethereum")
internal class AccountManagementServiceImpl(
    private val accountRepository: JpaAccountRepository,
    private val customerRepository: JpaCustomerRepository,
    private val movementRepository: JpaAccountMovementRepository,
    private val keyRepository: EthereumKeyVault,
    private val smallBank: SmallBank,
    private val clock: Clock
) : AccountManagementService {

    private val random = SecureRandom()

    override fun create(customerId: CustomerId): Account {
        val customer = customerRepository.findById(customerId.id).orElseThrow()

        // Ensure only one Ethereum account exists per customer
        if (customer.accounts.any { it.type == AccountType.ETHEREUM }) {
            throw IllegalStateException("Already existing account for customer: $customerId")
        }

        val keyPair = Keys.createEcKeyPair(random)
        keyRepository.store(Credentials.create(keyPair))

        val accountId = AccountId(keyPair.publicKey.toHexString())
        val account = Account(accountId, customerId, AccountType.ETHEREUM).toEntity(customer)

        return accountRepository.save(account).toPojo().also {
            subscribeToMovements(account)
        }
    }

    override fun find(accountId: AccountId): Account {
        return accountRepository.findById(accountId.id).orElseThrow().toPojo()
    }

    override fun list(customerId: CustomerId): List<Account> {
        customerRepository.findById(customerId.id).orElseThrow()
        return accountRepository.findByCustomerId(customerId.id).map { it.toPojo() }
    }

    override fun deposit(accountId: AccountId, amount: BigDecimal) {
        accountRepository.findById(accountId.id).orElseThrow()
        smallBank.deposit(amount.toBigInteger()).send()
    }

    override fun withdraw(accountId: AccountId, amount: BigDecimal) {
        accountRepository.findById(accountId.id).orElseThrow()
        smallBank.withdraw(amount.toBigInteger()).send()
    }

    override fun balance(accountId: AccountId): BigDecimal {
        accountRepository.findById(accountId.id).orElseThrow()
        return smallBank.balance().send().toBigDecimal()
    }

    // TODO Support date intervals
    override fun movements(accountId: AccountId): List<AccountMovement> {
        accountRepository.findById(accountId.id).orElseThrow()
        return movementRepository.findByAccountId(accountId.id).map { it.toPojo() }
    }

    /**
     * FIXME Filter events by account id.
     * FIXME Sync database only with delta.
     */
    private fun subscribeToMovements(account: PersistentAccount) {
        smallBank.accountDepositEventFlowable(EARLIEST, LATEST).subscribe {
            movementRepository.save(it.toEntity(account, clock))
        }
        smallBank.accountWithdrawalEventFlowable(EARLIEST, LATEST).subscribe {
            movementRepository.save(it.toEntity(account, clock))
        }
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
