package com.smallbank.infra.ethereum

import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigInteger

internal fun String.hexToBigInt(): BigInteger {
    return BigInteger(Numeric.cleanHexPrefix(this), 16)
}

internal fun BigInteger.toHexString(): String {
    return Numeric.encodeQuantity(this)
}

/**
 * Fixes Web3j error while parsing a hex value with trailing zeros.
 */
internal fun Web3j.ethGetBalance(address: String): BigInteger {
    return ethGetBalance(address, DefaultBlockParameterName.LATEST).send().result.hexToBigInt()
}

internal fun Int.toWei(unit: Convert.Unit): BigInteger {
    return Convert.toWei(toBigDecimal(), unit).toBigInteger()
}

internal fun Long.toWei(unit: Convert.Unit): BigInteger {
    return Convert.toWei(toBigDecimal(), unit).toBigInteger()
}

internal fun Float.toWei(unit: Convert.Unit): BigInteger {
    return Convert.toWei(toBigDecimal(), unit).toBigInteger()
}

internal fun Double.toWei(unit: Convert.Unit): BigInteger {
    return Convert.toWei(toBigDecimal(), unit).toBigInteger()
}
