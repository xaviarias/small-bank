package com.smallbank.infra.ethereum

import org.web3j.crypto.Credentials

/**
 * Customer key repository to store sensitive data externally.
 */
interface EthereumKeyVault {
    fun store(account: String, credentials: Credentials)
    fun resolve(account: String): Credentials?
}
