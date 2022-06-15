package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.protocol.Web3j
import org.web3j.protocol.exceptions.TransactionException
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest
class WithdrawTest : SmallBankTest() {

    @Test
    fun `withdraw from an empty account should throw an exception`() {
        val amount = 1.toWei(Convert.Unit.ETHER)

        Assertions.assertThrows(TransactionException::class.java) {
            smallBank.withdraw(amount).send()
        }
    }

    @Test
    fun `ether balances should be updated after withdrawal`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Retrieve initial balance
        // val initialBalance = web3j.ethGetBalance(SMALLBANK_ACCOUNT)

        // Deposit and withdraw 1 ETH to the bank
        val amount = 1.toWei(Convert.Unit.ETHER)
        val depositReceipt = smallBank.deposit(amount).send()
        val withdrawReceipt = smallBank.withdraw(amount).send()

        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)

        // val totalGas = (depositReceipt.gasUsed * gasProvider.getGasPrice("deposit")) +
        //        (withdrawReceipt.gasUsed * gasProvider.getGasPrice("withdraw"))

        // Balance should be the initial minus the gas costs
        // val expectedBalance = initialBalance - totalGas
        // val customerBalance = web3j.ethGetBalance(SMALLBANK_ACCOUNT)

        // FIXME Find out how to calculate the final tx costs
        // Assertions.assertEquals(expectedBalance, customerBalance)
    }
}
