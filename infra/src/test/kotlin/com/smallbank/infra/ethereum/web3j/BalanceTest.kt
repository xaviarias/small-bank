package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.toWei
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.utils.Convert
import java.math.BigInteger

@EVMTest
class BalanceTest : SmallBankTest() {

    @Test
    fun `balance should be zero after contract creation`() {
        Assertions.assertEquals(BigInteger.ZERO, smallBank.balance().send())
    }

    @Test
    fun `balance should increase after deposit`() {
        val amount = 1.toWei(Convert.Unit.ETHER)
        smallBank.deposit(amount).send()

        Assertions.assertEquals(amount, smallBank.balance().send())
    }
}
