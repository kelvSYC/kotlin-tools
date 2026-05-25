package com.kelvsyc.kotlin.core.traits.integral

import java.math.BigInteger

private val bigIntegerInstance: DivRem<BigInteger> = object : DivRem<BigInteger> {
    override fun BigInteger.divRem(other: BigInteger): DivRemResult<BigInteger> {
        val result = divideAndRemainder(other)
        return DivRemResult(result[0], result[1])
    }
}

val DivRem.Companion.bigInteger: DivRem<BigInteger> get() = bigIntegerInstance
