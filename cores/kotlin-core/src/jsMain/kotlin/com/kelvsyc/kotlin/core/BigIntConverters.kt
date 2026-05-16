package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.integral.SignedIntegral
import com.kelvsyc.kotlin.core.traits.integral.UnsignedIntegral

// ── BigInt range sentinels ────────────────────────────────────────────────────

private val longMin: BigInt = bigIntOf(Long.MIN_VALUE.toString())
private val longMax: BigInt = bigIntOf(Long.MAX_VALUE.toString())
private val intMin: BigInt = bigIntOf(Int.MIN_VALUE.toString())
private val intMax: BigInt = bigIntOf(Int.MAX_VALUE.toString())

// ── Narrowing helpers ─────────────────────────────────────────────────────────

private fun BigInt.toCheckedLong(): Long {
    if (this < longMin || this > longMax) throw ArithmeticException("BigInt out of Long range: ${toDecimalString()}")
    return toDecimalString().toLong()
}

private fun BigInt.toCheckedInt(): Int {
    if (this < intMin || this > intMax) throw ArithmeticException("BigInt out of Int range: ${toDecimalString()}")
    return toDecimalString().toInt()
}

// ── Converter instances ───────────────────────────────────────────────────────

private val longConverterInstance: Converter<BigInt, Long> = Converter.of(
    forward = { bigInt -> bigInt.toCheckedLong() },
    backward = { long -> bigIntOf(long.toString()) },
)

private val intConverterInstance: Converter<BigInt, Int> = Converter.of(
    forward = { bigInt -> bigInt.toCheckedInt() },
    backward = { int -> bigIntOf(int) },
)

val BigInt.Companion.longConverter: Converter<BigInt, Long> get() = longConverterInstance
val BigInt.Companion.intConverter: Converter<BigInt, Int> get() = intConverterInstance

// ── Widening extension functions ──────────────────────────────────────────────

/**
 * Returns a [BigInt] representing [value], widened from the signed integral type [T] via [Long].
 */
fun <T> SignedIntegral<T>.toBigInt(value: T): BigInt = bigIntOf(with(this) { value.toLong() }.toString())

/**
 * Returns a [BigInt] representing [value], widened from the unsigned integral type [T] via [ULong].
 */
fun <T> UnsignedIntegral<T>.toBigInt(value: T): BigInt = bigIntOf(with(this) { value.toULong() }.toString())
