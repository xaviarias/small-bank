package com.smallbank.infra.ethereum.web3j

import org.junit.jupiter.api.BeforeEach
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.FastRawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider

abstract class CustomerTest : SmallBankTest() {

    lateinit var customerSmallBank: SmallBank

    @BeforeEach
    override fun setUp(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        super.setUp(web3j, transactionManager, gasProvider)

        val customerTransactionManager = FastRawTransactionManager(web3j, Credentials.create(CUSTOMER_PRIVATE_KEY))
        customerSmallBank = SmallBank.load(smallBank.contractAddress, web3j, customerTransactionManager, gasProvider)
    }
}
