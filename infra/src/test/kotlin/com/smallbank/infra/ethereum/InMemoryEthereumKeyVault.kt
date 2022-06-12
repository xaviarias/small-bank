package com.smallbank.infra.ethereum

import com.smallbank.domain.model.customer.CustomerId
import org.springframework.stereotype.Repository

/**
 * Customer key repository for testing.
 */
@Repository
class InMemoryEthereumKeyVault : EthereumKeyVault {

    private val keys = mapOf(
        CustomerId("627306090abaB3A6e1400e9345bC60c78a8BEf57") to "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3"
    )

    override fun resolve(customerId: CustomerId): String? = keys[customerId]
}
