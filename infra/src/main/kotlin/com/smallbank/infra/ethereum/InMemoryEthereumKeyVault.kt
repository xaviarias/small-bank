package com.smallbank.infra.ethereum

import org.springframework.stereotype.Repository
import org.web3j.crypto.Credentials
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Default in-memory Ethereum key vault.
 *
 * In a real scenario this would typically be an external system, e.g. KMS.
 */
@Repository
internal open class InMemoryEthereumKeyVault : EthereumKeyVault {

    private val keys: ConcurrentMap<String, Credentials> = ConcurrentHashMap()

    override fun store(account: String, credentials: Credentials) {
        keys.putIfAbsent(account, credentials)
    }

    override fun resolve(account: String) = keys[account]
}
