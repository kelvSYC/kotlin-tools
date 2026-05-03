package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import com.kelvsyc.kotlin.core.traits.fp.binaryPow
import java.math.BigInteger

private val bigIntegerInstance: IntegerPower<BigInteger> = object : IntegerPower<BigInteger> {
    override fun BigInteger.pow(n: Int): BigInteger = binaryPow(this, n, BigInteger.ONE) { a, b -> a * b }
}

val IntegerPower.Companion.bigInteger: IntegerPower<BigInteger> get() = bigIntegerInstance
