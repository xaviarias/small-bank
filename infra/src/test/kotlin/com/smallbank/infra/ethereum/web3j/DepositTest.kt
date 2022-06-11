package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert

@EVMTest
class DepositTest : SmallBankTest() {

    @Test
    fun `ether balances should be updated after money deposit`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Retrieve initial balance
        val initialBalance = web3j.ethGetBalance(SMALLBANK_ADDRESS)

        // Deposit 1 ETH to the bank
        val amount = 1.toWei(Convert.Unit.ETHER)
        val receipt = smallBank.deposit(amount).send()

        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(amount, contractBalance)

        val totalGas = receipt.gasUsed * gasProvider.getGasPrice("deposit")
        val expectedBalance = initialBalance - (amount + totalGas)
        val customerBalance = web3j.ethGetBalance(SMALLBANK_ADDRESS)
        Assertions.assertEquals(expectedBalance, customerBalance)
    }
}
