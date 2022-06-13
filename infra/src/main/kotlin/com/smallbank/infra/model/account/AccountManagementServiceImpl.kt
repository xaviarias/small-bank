package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.toHexString
import com.smallbank.infra.ethereum.web3j.SmallBank
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import java.math.BigDecimal
import java.security.SecureRandom
import java.time.Period

@Service
@Qualifier("ethereum")
internal class AccountManagementServiceImpl(
    private val accountRepository: AccountRepository,
    private val keyRepository: EthereumKeyVault,
    private val smallBank: SmallBank
) : AccountManagementService {

    private val random = SecureRandom()

    override fun create(customerId: CustomerId): Account {
        checkAlreadyExistingAccount(customerId)

        val keyPair = Keys.createEcKeyPair(random)
        keyRepository.store(Credentials.create(keyPair))

        val accountId = keyPair.publicKey.toHexString()

        return Account(AccountId(accountId), customerId, AccountType.ETHEREUM).also {
            accountRepository.create(it)
        }
    }

    override fun list(customerId: CustomerId): List<Account> {
        return accountRepository.findByCustomer(customerId)
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
//        val deposits = contract(customerId).accountDepositEventFlowable(
//            DefaultBlockParameterName.EARLIEST,
//            DefaultBlockParameterName.LATEST
//        ).blockingIterable().map {
//            AccountMovement(MovementType.DEPOSIT, it.amount.toBigDecimal())
//        }
        return listOf()
    }

    /**
     * Ensure only one Ethereum account exists per customer.
     */
    private fun checkAlreadyExistingAccount(customerId: CustomerId) {
        if (list(customerId).any { it.type == AccountType.ETHEREUM }) {
            throw IllegalStateException("Already existing account for customer: $customerId")
        }
    }
}
