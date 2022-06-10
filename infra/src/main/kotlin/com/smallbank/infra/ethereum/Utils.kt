package com.smallbank.infra.ethereum

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Numeric.cleanHexPrefix
import java.math.BigInteger

internal fun String.hexToBigInt(): BigInteger {
    return BigInteger(cleanHexPrefix(this), 16)
}

/**
 * Fixes Web3j error while parsing a hex value with trailing zeros.
 */
internal fun Web3j.ethGetBalance(address: String): BigInteger {
    return ethGetBalance(address, DefaultBlockParameterName.LATEST).send().result.hexToBigInt()
}
