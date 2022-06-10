package com.smallbank.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.FastRawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest(NodeType.BESU)
class SmallBankTest {

    private lateinit var smallBank: SmallBank
    private lateinit var customerSmallBank: SmallBank

    @BeforeEach
    fun setUp(
        web3j: Web3j,
        transactionManager: TransactionManager,
        gasProvider: ContractGasProvider
    ) {
        smallBank = SmallBank.deploy(web3j, transactionManager, gasProvider).send()

        val customerTransactionManager = FastRawTransactionManager(web3j, Credentials.create(CUSTOMER_PRIVATE_KEY))
        customerSmallBank = SmallBank.load(smallBank.contractAddress, web3j, customerTransactionManager, gasProvider)
    }

    @Test
    fun `contract owner is initialized after creation`() {
        Assertions.assertEquals(SMALLBANK_ADDRESS, smallBank.owner().send())
    }

    @Test
    fun `contract balance is zero after creation`(web3j: Web3j) {
        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)
    }

    @Test
    fun `ether balances are updated after money deposit`(
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

    companion object {
        const val SMALLBANK_ADDRESS = "0xfe3b557e8fb62b89f4916b721be55ceb828dbd73"

        const val CUSTOMER_ADDRESS = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57"

        const val CUSTOMER_PRIVATE_KEY = "0xc87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3"

        val CUSTOMER_INITIAL_BALANCE = "90000000000000000000000".toBigInteger()
    }
}
