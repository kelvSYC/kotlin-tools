package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.BigInt

private val bigIntGcdInstance: Gcd<BigInt> by lazy { Gcd.from(SignedIntegerArithmetic.bigInt) }

val Gcd.Companion.bigInt: Gcd<BigInt> get() = bigIntGcdInstance
