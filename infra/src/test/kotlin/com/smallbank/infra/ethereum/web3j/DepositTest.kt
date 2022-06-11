package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert

@EVMTest(NodeType.BESU)
class DepositTest : CustomerTest() {

    @Test
    fun `ether balances should be updated after money deposit`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Check initial balances
        Assertions.assertEquals(CUSTOMER_INITIAL_BALANCE, web3j.ethGetBalance(CUSTOMER_ADDRESS))

        // Deposit 1 ETH to the bank
        val amount = Convert.toWei(1.toBigDecimal(), Convert.Unit.ETHER).toBigInteger()
        val receipt = customerSmallBank.deposit(amount).send()

        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(amount, contractBalance)

        val totalGas = receipt.gasUsed * gasProvider.getGasPrice("deposit")
        val expectedBalance = CUSTOMER_INITIAL_BALANCE - (amount + totalGas)
        val customerBalance = web3j.ethGetBalance(CUSTOMER_ADDRESS)
        Assertions.assertEquals(expectedBalance, customerBalance)
    }
}
