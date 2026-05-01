package com.kelvsyc.kotlin.core.traits

import java.math.BigInteger

// ── SignedIntegerArithmetic<BigInteger> ───────────────────────────────────────
//
// BigInteger is a Java class; all operations below are Java member functions on BigInteger, so
// they always take dispatch priority over the same-named member extensions from the trait
// interface when called via `with(ops)` — no recursion risk, and no override is needed.
//
// divide: BigInteger.divide performs truncated-integer division (toward zero), matching the
// IntegerArithmetic contract. Both divide and remainder throw ArithmeticException on zero divisor.
//
// remainder: BigInteger.remainder returns a result with the sign of the dividend, matching the
// truncated-division semantics required by IntegerArithmetic.rem.
//
// Dispatch limitation — mod:
//   BigInteger.mod(BigInteger) is a Java member that only accepts a positive modulus; it shadows
//   the trait's default mod() at call sites. Callers needing floor-mod with a negative divisor
//   must compute it manually via floorDiv + remainder.
//   Note: the trait uses sign() rather than signum() specifically to avoid a similar clash with
//   BigInteger.signum(): Int.

private val bigIntegerSignedInstance: SignedIntegerArithmetic<BigInteger> =
    object : SignedIntegerArithmetic<BigInteger> {
        override val zero: BigInteger get() = BigInteger.ZERO
        override val one: BigInteger get() = BigInteger.ONE

        override fun BigInteger.add(other: BigInteger): BigInteger = add(other)
        override fun BigInteger.subtract(other: BigInteger): BigInteger = subtract(other)
        override fun BigInteger.multiply(other: BigInteger): BigInteger = multiply(other)
        override fun BigInteger.divide(other: BigInteger): BigInteger = divide(other)
        override fun BigInteger.rem(other: BigInteger): BigInteger = remainder(other)
        override fun BigInteger.compareTo(other: BigInteger): Int = compareTo(other)
        override fun BigInteger.negate(): BigInteger = negate()
        override fun BigInteger.abs(): BigInteger = abs()
    }

val SignedIntegerArithmetic.Companion.bigInteger: SignedIntegerArithmetic<BigInteger>
    get() = bigIntegerSignedInstance

// ── ArithmeticRightShift<BigInteger> ─────────────────────────────────────────
//
// BigInteger.shiftRight(n) is an arithmetic (sign-extending) right shift by definition,
// matching the ArithmeticRightShift contract exactly.

private val bigIntegerArithmeticRightShiftInstance: ArithmeticRightShift<BigInteger> =
    object : ArithmeticRightShift<BigInteger> {
        override fun BigInteger.arithmeticRightShift(bits: Int): BigInteger = shiftRight(bits)
    }

val ArithmeticRightShift.Companion.bigInteger: ArithmeticRightShift<BigInteger>
    get() = bigIntegerArithmeticRightShiftInstance
