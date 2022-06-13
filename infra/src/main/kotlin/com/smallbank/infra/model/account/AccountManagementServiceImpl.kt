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
import org.web3j.protocol.core.DefaultBlockParameterName
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.Period
import java.util.UUID
import javax.annotation.PostConstruct

@Service
@Qualifier("ethereum")
internal class AccountManagementServiceImpl(
    private val accountRepository: JpaAccountRepository,
    private val customerRepository: JpaCustomerRepository,
    private val movementRepository: JpaAccountMovementRepository,
    private val keyRepository: EthereumKeyVault,
    private val smallBank: SmallBank
) : AccountManagementService {

    private val random = SecureRandom()

    @PostConstruct
    fun subscribeToMovements() {
        smallBank.accountDepositEventFlowable(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST
        ).subscribe {
            movementRepository.save(it.toEntity(accountRepository))
        }
        smallBank.accountWithdrawalEventFlowable(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST
        ).subscribe {
            movementRepository.save(it.toEntity(accountRepository))
        }
    }

    override fun create(customerId: CustomerId): Account {
        checkAlreadyExistingAccount(customerId)

        val keyPair = Keys.createEcKeyPair(random)
        keyRepository.store(Credentials.create(keyPair))

        val accountId = keyPair.publicKey.toHexString()
        return Account(AccountId(accountId), customerId, AccountType.ETHEREUM).also {
            accountRepository.save(it.toEntity(customerRepository, movementRepository))
        }
    }

    override fun list(customerId: CustomerId): List<Account> {
        return accountRepository.findByCustomerId(customerId.id).map { it.toPojo() }
    }

    override fun deposit(accountId: AccountId, amount: BigDecimal) {
        smallBank.deposit(amount.toBigInteger()).send()
    }

    override fun withdraw(accountId: AccountId, amount: BigDecimal) {
        smallBank.withdraw(amount.toBigInteger()).send()
    }

    override fun balance(accountId: AccountId): BigDecimal {
        return smallBank.balance().send().toBigDecimal()
    }

    // TODO Support date intervals
    override fun movements(accountId: AccountId, period: Period): List<AccountMovement> {
        return movementRepository.findByAccountId(accountId.id).map { it.toPojo() }
    }

    /**
     * Ensure only one Ethereum account exists per customer.
     */
    private fun checkAlreadyExistingAccount(customerId: CustomerId) {
        if (list(customerId).any { it.type == AccountType.ETHEREUM }) {
            throw IllegalStateException("Already existing account for customer: $customerId")
        }
    }

    private fun AccountDepositEventResponse.toEntity(
        accountRepository: JpaAccountRepository
    ) = PersistentAccountMovement(
        id = UUID.randomUUID().toString(),
        timestamp = LocalDateTime.now(),
        account = accountRepository.findById(customer).orElseThrow(),
        type = MovementType.DEPOSIT,
        amount = amount.toBigDecimal()
    )

    private fun AccountWithdrawalEventResponse.toEntity(
        accountRepository: JpaAccountRepository
    ) = PersistentAccountMovement(
        id = UUID.randomUUID().toString(),
        timestamp = LocalDateTime.now(),
        account = accountRepository.findById(customer).orElseThrow(),
        type = MovementType.WITHDRAW,
        amount = amount.toBigDecimal()
    )
}
