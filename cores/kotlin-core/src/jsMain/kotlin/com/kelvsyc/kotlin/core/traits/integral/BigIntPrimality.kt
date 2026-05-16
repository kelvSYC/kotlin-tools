package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.compareTo
import com.kelvsyc.kotlin.core.div
import com.kelvsyc.kotlin.core.minus
import com.kelvsyc.kotlin.core.rem
import com.kelvsyc.kotlin.core.times

// ── Modular exponentiation ────────────────────────────────────────────────────
//
// BigInt is arbitrary-precision, so there's no overflow risk — we can multiply directly.
// Repeated squaring keeps intermediate values bounded by mod².

private fun modPow(base: BigInt, exp: BigInt, mod: BigInt): BigInt {
    val zero = bigIntOf(0)
    val one = bigIntOf(1)
    val two = bigIntOf(2)
    var result = one
    var b = base % mod
    var e = exp
    while (e > zero) {
        if (e % two > zero) result = result * b % mod
        b = b * b % mod
        e = e / two
    }
    return result
}

// ── Miller-Rabin for a single witness ────────────────────────────────────────

private fun millerRabinBigInt(n: BigInt, a: BigInt): Boolean {
    if (n == a) return true
    val zero = bigIntOf(0)
    val one = bigIntOf(1)
    val two = bigIntOf(2)
    var d = n - one
    var r = 0
    while (d % two == zero) { d = d / two; r++ }
    var x = modPow(a, d, n)
    val nMinus1 = n - one
    if (x == one || x == nMinus1) return true
    repeat(r - 1) {
        x = x * x % n
        if (x == nMinus1) return true
    }
    return false
}

// ── Witness set ───────────────────────────────────────────────────────────────
//
// {2,3,5,7,11,13,17,19,23,29,31,37} is deterministically correct for all n < 3.3×10²⁴,
// which covers all values representable as Long and far beyond.

private val bigIntWitnesses: List<BigInt> = listOf(
    bigIntOf(2), bigIntOf(3), bigIntOf(5), bigIntOf(7),
    bigIntOf(11), bigIntOf(13), bigIntOf(17), bigIntOf(19),
    bigIntOf(23), bigIntOf(29), bigIntOf(31), bigIntOf(37),
)

// ── Primality<BigInt> instance ────────────────────────────────────────────────

private val bigIntPrimalityInstance: Primality<BigInt> = object : Primality<BigInt> {
    override fun BigInt.isPrime(): Boolean {
        val zero = bigIntOf(0)
        val two = bigIntOf(2)
        val three = bigIntOf(3)
        val four = bigIntOf(4)
        if (this < two) return false
        if (this < four) return true          // 2 and 3 are prime
        if (this % two == zero || this % three == zero) return false
        return bigIntWitnesses.all { millerRabinBigInt(this, it) }
    }
}

val Primality.Companion.bigInt: Primality<BigInt> get() = bigIntPrimalityInstance
