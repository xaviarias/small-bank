package com.smallbank.infra.ethereum

import org.web3j.crypto.Credentials

/**
 * Customer key repository to store sensitive data externally.
 */
internal interface EthereumKeyVault {
    fun store(credentials: Credentials)
    fun resolve(account: String): Credentials?
}
