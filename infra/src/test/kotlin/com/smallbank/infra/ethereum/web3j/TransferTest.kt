package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.crypto.Keys
import org.web3j.crypto.Wallet
import org.web3j.protocol.Web3j
import org.web3j.protocol.exceptions.TransactionException
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest
class TransferTest : SmallBankTest() {

    private val transferAccount: String by lazy {
        val ecKeyPair = Keys.createEcKeyPair()
        Wallet.createLight("", ecKeyPair).address
    }

    @Test
    fun `transfer from an empty account should throw an exception`() {
        val amount = 1.toWei(Convert.Unit.ETHER)

        Assertions.assertThrows(TransactionException::class.java) {
            smallBank.transfer(transferAccount, amount).send()
        }
    }

    @Test
    fun `ether balances should be updated after transfer`(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        // Retrieve initial balance
        // val initialBalance = web3j.ethGetBalance(SMALLBANK_ACCOUNT)

        // Deposit 1 ETH to the bank
        val amount = 1.toWei(Convert.Unit.ETHER)
        val depositReceipt = smallBank.deposit(amount).send()

        // Transfer to another account
        val transferReceipt = smallBank.transfer(transferAccount, amount).send()
        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)

        // val totalGas = (depositReceipt.gasUsed * gasProvider.getGasPrice("deposit")) +
        //        (transferReceipt.gasUsed * gasProvider.getGasPrice("transfer"))

        // Balance should be the initial minus the gas costs
        // val expectedBalance = initialBalance - (totalGas + amount)
        // val customerBalance = web3j.ethGetBalance(SMALLBANK_ACCOUNT)

        // FIXME Find out how to calculate the final tx costs
        // Assertions.assertEquals(expectedBalance, customerBalance)

        // Verify the funds have been transferred
        val transferredBalance = web3j.ethGetBalance(transferAccount)
        Assertions.assertEquals(amount, transferredBalance)
    }
}
