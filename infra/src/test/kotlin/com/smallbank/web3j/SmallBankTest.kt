package com.smallbank.web3j

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider

@EVMTest
class SmallBankTest {

    @Test
    fun `realize money deposit`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        val balance = SmallBank.deploy(web3j, transactionManager, gasProvider).send().run {
            deposit(30.toBigInteger()).send()
            balance().send()
        }

        Assertions.assertEquals(30.toBigInteger(), balance)
    }
}

