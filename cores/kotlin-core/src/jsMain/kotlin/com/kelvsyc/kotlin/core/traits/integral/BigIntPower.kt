package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.times
import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import com.kelvsyc.kotlin.core.traits.fp.binaryPow

private val bigIntPowerInstance: IntegerPower<BigInt> = object : IntegerPower<BigInt> {
    override fun BigInt.pow(n: Int): BigInt = binaryPow(this, n, bigIntOf(1)) { a, b -> a * b }
}

val IntegerPower.Companion.bigInt: IntegerPower<BigInt> get() = bigIntPowerInstance
