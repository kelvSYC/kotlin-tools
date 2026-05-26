package com.kelvsyc.kotlin.core.traits.fp

import java.math.BigDecimal
import java.math.RoundingMode

// ── FloatingPointRounding<BigDecimal> ─────────────────────────────────────────
//
// BigDecimal.setScale(0, RoundingMode) is a Java member. Inside the override bodies, calling
// setScale() resolves to the Java member — no dispatch conflict with the trait extensions.
//
// The result has scale 0 (i.e. an integer-valued BigDecimal). The sign and unscaled value are
// preserved: floor(-1.5) == -2, ceil(-1.5) == -1.

private val bigDecimalRoundingInstance: FloatingPointRounding<BigDecimal> =
    object : FloatingPointRounding<BigDecimal> {
        override fun BigDecimal.floor(): BigDecimal = setScale(0, RoundingMode.FLOOR)
        override fun BigDecimal.ceil(): BigDecimal = setScale(0, RoundingMode.CEILING)
        override fun BigDecimal.trunc(): BigDecimal = setScale(0, RoundingMode.DOWN)
        override fun BigDecimal.roundUp(): BigDecimal = setScale(0, RoundingMode.UP)
    }

val FloatingPointRounding.Companion.bigDecimal: FloatingPointRounding<BigDecimal>
    get() = bigDecimalRoundingInstance

// ── FloatingPointScald<BigDecimal> ────────────────────────────────────────────
//
// BigDecimal.scaleByPowerOfTen(n) computes this × 10^n, matching the FloatingPointScald
// contract exactly. It is a Java member, so scaleByPowerOfTen() inside the override body
// resolves to the Java member — no dispatch conflict.

private val bigDecimalScaldInstance: FloatingPointScald<BigDecimal> =
    object : FloatingPointScald<BigDecimal> {
        override fun BigDecimal.scald(n: Int): BigDecimal = scaleByPowerOfTen(n)
    }

val FloatingPointScald.Companion.bigDecimal: FloatingPointScald<BigDecimal>
    get() = bigDecimalScaldInstance

// ── IntegerPower<BigDecimal> ──────────────────────────────────────────────────
//
// BigDecimal.pow(int) is a Java member implementing exact integer exponentiation. It throws
// ArithmeticException for negative exponents; the require() here converts that to
// IllegalArgumentException to match the IntegerPower contract.

private val bigDecimalPowerInstance: IntegerPower<BigDecimal> = object : IntegerPower<BigDecimal> {
    override fun BigDecimal.pow(n: Int): BigDecimal {
        require(n >= 0) { "Exponent must be non-negative, got $n" }
        return pow(n)
    }
}

val IntegerPower.Companion.bigDecimal: IntegerPower<BigDecimal>
    get() = bigDecimalPowerInstance
