package com.smallbank.infra.ethereum

import com.smallbank.domain.model.customer.CustomerId
import org.springframework.stereotype.Repository

/**
 * Customer key repository for testing.
 */
@Repository
class InMemoryEthereumKeyVault : EthereumKeyVault {

    private val keys = mapOf(
        CustomerId("fe3b557e8fb62b89f4916b721be55ceb828dbd73") to "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63",
        CustomerId("627306090abaB3A6e1400e9345bC60c78a8BEf57") to "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3"
    )

    override fun resolve(customerId: CustomerId): String? = keys[customerId]
}
