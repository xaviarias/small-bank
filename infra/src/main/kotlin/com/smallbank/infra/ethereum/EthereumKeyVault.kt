package com.smallbank.infra.ethereum

import com.smallbank.domain.model.customer.CustomerId

/**
 * Customer key repository to store sensitive data externally.
 */
internal interface EthereumKeyVault {
    fun resolve(customerId: CustomerId): String?
}
