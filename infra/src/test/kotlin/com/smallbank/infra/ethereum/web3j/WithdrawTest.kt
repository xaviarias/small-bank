package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.protocol.Web3j
import org.web3j.protocol.exceptions.TransactionException
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest(NodeType.BESU)
class WithdrawTest : CustomerTest() {

    @Test
    fun `withdraw from an empty account should throw an exception`() {
        val amount = 1.toWei(Convert.Unit.ETHER)

        Assertions.assertThrows(TransactionException::class.java) {
            customerSmallBank.withdraw(amount).send()
        }
    }

    @Test
    fun `ether balances should be updated after withdrawal`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Deposit and withdraw 1 ETH to the bank
        val amount = 1.toWei(Convert.Unit.ETHER)
        val depositReceipt = customerSmallBank.deposit(amount).send()
        val withdrawReceipt = customerSmallBank.withdraw(amount).send()

        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)

        val totalGas = (depositReceipt.gasUsed + withdrawReceipt.gasUsed) *
                gasProvider.getGasPrice("withdrawal")

        // Balance should be the initial minus the gas costs
        val expectedBalance = CUSTOMER_INITIAL_BALANCE - totalGas
        val customerBalance = web3j.ethGetBalance(CUSTOMER_ADDRESS)
        Assertions.assertEquals(expectedBalance, customerBalance)
    }
}
