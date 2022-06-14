package com.smallbank.infra.ethereum

import com.smallbank.domain.model.account.Account.AccountType
import com.smallbank.domain.model.customer.Customer
import com.smallbank.infra.ethereum.web3j.SmallBank
import com.smallbank.infra.model.account.AccountRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jService
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider

@Component
internal class EthereumConfiguration(
    private val keyVault: EthereumKeyVault,
    private val accountRepository: AccountRepository
) {
    private lateinit var web3j: Web3j
    private lateinit var gasProvider: ContractGasProvider

    @Value("\${smallbank.ethereum.url:#{null}}")
    private var url: String? = HttpService.DEFAULT_URL

    @Value("\${smallbank.ethereum.account:#{null}}")
    private var account: String? = null

    @Value("\${smallbank.ethereum.contract.address:#{null}}")
    private var contractAddress: String? = null

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3j(service: Web3jService): Web3j {
        web3j = Web3j.build(service)
        return web3j
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3jService(): Web3jService {
        return HttpService(url)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun gasProvider(): ContractGasProvider {
        gasProvider = DefaultGasProvider()
        return gasProvider
    }

    @Lazy
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    fun smallBank(
        web3j: Web3j,
        gasProvider: ContractGasProvider
    ): SmallBank {
        val credentials = contextCredentials()

        if (contractAddress.isNullOrBlank()) {
            contractAddress = deployContract()
        }
        return loadContract(web3j, gasProvider, credentials)
    }

    /**
     * Resolves authenticated customer's credentials using the key vault.
     *
     * @return The authenticated customer Ethereum account,
     *         or `null` if there is no authenticated customer.
     */
    private fun contextCredentials(): Credentials {
        return with(SecurityContextHolder.getContext()) {
            requireNotNull(authentication) {
                throw IllegalStateException("No credentials found in security context")
            }

            val customer = authentication.principal as Customer // FIXME User
            val account = accountRepository.findByCustomer(customer.id).firstOrNull {
                it.type == AccountType.ETHEREUM
            }

            requireNotNull(account) {
                throw IllegalStateException("No Ethereum account found for customer:${customer.id}")
            }

            keyVault.resolve(account.id.id) ?: throw IllegalStateException(
                "Customer account credentials not found in key vault: $account"
            )
        }
    }

    private fun deployContract(): String {
        require(account != null) {
            throw IllegalStateException("Ethereum account not initialized")
        }
        val credentials = keyVault.resolve(account!!).apply {
            requireNotNull(this) { "SmallBank credentials not found: $account" }
        }
        return SmallBank.deploy(
            web3j,
            credentials,
            gasProvider
        ).send().contractAddress
    }

    private fun loadContract(
        web3j: Web3j,
        gasProvider: ContractGasProvider,
        credentials: Credentials
    ): SmallBank {
        return SmallBank.load(
            contractAddress,
            web3j,
            credentials,
            gasProvider
        )
    }
}
