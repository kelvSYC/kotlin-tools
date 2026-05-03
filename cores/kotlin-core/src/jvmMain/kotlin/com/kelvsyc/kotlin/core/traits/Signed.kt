@file:JvmName("SignedJvmInstances")

package com.kelvsyc.kotlin.core.traits

import java.math.BigDecimal
import java.math.BigInteger

// ── BigInteger ────────────────────────────────────────────────────────────────
//
// BigInteger is a Java class; negate() and abs() are Java member functions and
// always take dispatch priority over the same-named member extensions from the
// trait interface — no recursion risk. See BigIntegerArithmetic.kt for the same
// pattern applied to the full arithmetic trait.
//
// Alternatively, Signed.bigInteger can be obtained via SignedIntegerArithmetic.bigInteger
// since that instance also satisfies Signed<BigInteger>. The dedicated instance below
// avoids pulling in the full arithmetic machinery for callers that only need sign ops.

private val bigIntegerInstance: Signed<BigInteger> = object : Signed<BigInteger> {
    override fun BigInteger.isNegative(): Boolean = signum() < 0
    override fun BigInteger.isPositive(): Boolean = signum() > 0
    override fun BigInteger.negate(): BigInteger = negate()
    override fun BigInteger.abs(): BigInteger = abs()
}

// ── BigDecimal ────────────────────────────────────────────────────────────────
//
// Same dispatch reasoning as BigInteger: negate(), abs(), and signum() are all
// Java member functions on BigDecimal.

private val bigDecimalInstance: Signed<BigDecimal> = object : Signed<BigDecimal> {
    override fun BigDecimal.isNegative(): Boolean = signum() < 0
    override fun BigDecimal.isPositive(): Boolean = signum() > 0
    override fun BigDecimal.negate(): BigDecimal = negate()
    override fun BigDecimal.abs(): BigDecimal = abs()
}

val Signed.Companion.bigInteger: Signed<BigInteger> get() = bigIntegerInstance
val Signed.Companion.bigDecimal: Signed<BigDecimal> get() = bigDecimalInstance
