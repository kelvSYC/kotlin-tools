package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.SignedIntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.bigInt

private val bigIntRationalInstance: RationalArithmetic<Rational<BigInt>, BigInt> by lazy {
    RationalArithmetic.from(SignedIntegerArithmetic.bigInt, Gcd.bigInt)
}

val RationalArithmetic.Companion.bigInt: RationalArithmetic<Rational<BigInt>, BigInt>
    get() = bigIntRationalInstance
