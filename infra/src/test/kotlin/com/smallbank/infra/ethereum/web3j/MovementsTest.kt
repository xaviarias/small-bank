package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.toWei
import com.smallbank.infra.ethereum.web3j.SmallBank.AccountDepositEventResponse
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Convert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

@EVMTest(NodeType.BESU)
class MovementsTest : SmallBankTest() {

    @Test
    fun `movements should list deposits and withdrawals`() {
        val countDownLatch = CountDownLatch(2)
        val depositRef = AtomicReference<AccountDepositEventResponse>()
        val withdrawRef = AtomicReference<AccountDepositEventResponse>()

        smallBank.accountDepositEventFlowable(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST
        ).subscribe {
            depositRef.set(it)
            countDownLatch.countDown()
        }
        smallBank.accountDepositEventFlowable(
            DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.LATEST
        ).subscribe {
            withdrawRef.set(it)
            countDownLatch.countDown()
        }

        // Deposit and withdraw 1 ETH to the bank
        val amount = 1.toWei(Convert.Unit.ETHER)
        smallBank.deposit(amount).send()
        smallBank.withdraw(amount).send()

        countDownLatch.await()
    }
}
