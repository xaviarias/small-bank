package com.smallbank.infra.ethereum.web3j

import org.junit.jupiter.api.BeforeEach
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider

abstract class SmallBankTest {

    lateinit var smallBank: SmallBank

    @BeforeEach
    open fun setUp(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        smallBank = SmallBank.deploy(web3j, transactionManager, gasProvider).send()
    }
}
