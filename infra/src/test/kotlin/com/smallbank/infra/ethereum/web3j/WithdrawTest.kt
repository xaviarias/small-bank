package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest(NodeType.BESU)
class WithdrawTest : CustomerTest() {

    @Test
    fun `withdraw more than zero from an empty account should throw an exception`() {
        val amount = 1.toWei(Convert.Unit.ETHER)

        Assertions.assertThrows(Exception::class.java) {
            customerSmallBank.withdraw(amount).send()
        }
    }

    @Test
    fun `ether balances should be updated after money withdrawal`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Deposit 1 ETH to the bank
        val amount = Convert.toWei(1.toBigDecimal(), Convert.Unit.ETHER).toBigInteger()
        val depositReceipt = customerSmallBank.deposit(amount).send()
        val withdrawReceipt = customerSmallBank.withdraw(amount).send()

        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)

        val totalGas = (depositReceipt.gasUsed + withdrawReceipt.gasUsed) *
                gasProvider.getGasPrice("deposit")

        val expectedBalance = CUSTOMER_INITIAL_BALANCE - (amount + totalGas)
        val customerBalance = web3j.ethGetBalance(CUSTOMER_ADDRESS)
        Assertions.assertEquals(expectedBalance, customerBalance)
    }
}
