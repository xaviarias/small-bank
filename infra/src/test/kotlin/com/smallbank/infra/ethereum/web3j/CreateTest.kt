package com.smallbank.infra.ethereum.web3j

import com.smallbank.infra.ethereum.ethGetBalance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.web3j.EVMTest
import org.web3j.protocol.Web3j
import java.math.BigInteger

@EVMTest
class CreateTest : SmallBankTest() {

    @Test
    fun `contract owner should be initialized after creation`() {
        Assertions.assertEquals(SMALLBANK_ACCOUNT, smallBank.owner().send())
    }

    @Test
    fun `contract balance should be zero after creation`(web3j: Web3j) {
        val contractBalance = web3j.ethGetBalance(smallBank.contractAddress)
        Assertions.assertEquals(BigInteger.ZERO, contractBalance)
    }
}
