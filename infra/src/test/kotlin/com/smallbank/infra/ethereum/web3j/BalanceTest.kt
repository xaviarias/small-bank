package com.smallbank.infra.ethereum.web3j

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import java.math.BigInteger

@EVMTest
class BalanceTest : SmallBankTest() {

    @Test
    fun `balance should be zero after contract creation`() {
        Assertions.assertEquals(BigInteger.ZERO, smallBank.balance().send())
    }
}
