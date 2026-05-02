package com.kelvsyc.kotlin.core.traits.integral

/**
 * `Primality` is a composable trait providing integer primality testing on values of type [T].
 *
 * [isPrime] returns `true` if the receiver is a prime number. Non-positive values always return `false`.
 *
 * Companions are provided for the types where primality testing is practically relevant:
 * - [Companion.int] — uses deterministic Miller-Rabin with witnesses {2, 7, 61}, correct for all [Int] values
 * - [Companion.long] — uses deterministic Miller-Rabin with witnesses {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37},
 *   correct for all [Long] values
 * - [Companion.uint] — delegates to the [Long] path after widening; correct for all [UInt] values
 * - [Companion.ulong] — available on JVM only, using [java.math.BigInteger]-backed modular exponentiation
 *
 * Smaller types (Byte, Short, UByte, UShort) are omitted: their ranges are too small for Miller-Rabin
 * to matter, and practical call sites for primality at those widths do not exist.
 *
 * An instance can also be derived from any [SignedIntegral] via [Companion.from]; all signed values
 * are widened to [Long] before testing.
 */
interface Primality<T> {
    companion object

    fun T.isPrime(): Boolean
}

// ── Miller-Rabin internals ────────────────────────────────────────────────────

// Binary modular multiplication a*b mod m.
// Requires m <= Long.MAX_VALUE so that a < m < 2^63, ensuring a+a and result+a never overflow ULong.
private fun mulmod(a: ULong, b: ULong, m: ULong): ULong {
    var result = 0uL
    var x = a % m
    var y = b
    while (y > 0uL) {
        if (y and 1uL != 0uL) result = (result + x) % m
        x = (x + x) % m
        y = y shr 1
    }
    return result
}

private fun powmod(base: ULong, exp: ULong, mod: ULong): ULong {
    var result = 1uL
    var b = base % mod
    var e = exp
    while (e > 0uL) {
        if (e and 1uL != 0uL) result = mulmod(result, b, mod)
        b = mulmod(b, b, mod)
        e = e shr 1
    }
    return result
}

private fun millerRabin(n: ULong, a: ULong): Boolean {
    if (n == a) return true
    var d = n - 1uL
    var r = 0
    while (d and 1uL == 0uL) { d = d shr 1; r++ }
    var x = powmod(a, d, n)
    if (x == 1uL || x == n - 1uL) return true
    repeat(r - 1) {
        x = mulmod(x, x, n)
        if (x == n - 1uL) return true
    }
    return false
}

// Internal so jvmMain can call it for the lower half of ULong (values <= Long.MAX_VALUE).
internal fun longIsPrime(n: Long): Boolean {
    if (n < 2L) return false
    if (n < 4L) return true
    if (n % 2L == 0L || n % 3L == 0L) return false
    val u = n.toULong()
    return millerRabin(u, 2uL) && millerRabin(u, 3uL) && millerRabin(u, 5uL) &&
        millerRabin(u, 7uL) && millerRabin(u, 11uL) && millerRabin(u, 13uL) &&
        millerRabin(u, 17uL) && millerRabin(u, 19uL) && millerRabin(u, 23uL) &&
        millerRabin(u, 29uL) && millerRabin(u, 31uL) && millerRabin(u, 37uL)
}

// ── Factory ───────────────────────────────────────────────────────────────────

/**
 * Returns a [Primality] instance derived from [integral] for a signed integral type.
 *
 * Values are widened to [Long] via [SignedIntegral.toLong] before testing. This is correct for
 * all signed primitive integral types ([Byte], [Short], [Int], [Long]).
 */
fun <T> Primality.Companion.from(integral: SignedIntegral<T>): Primality<T> = object : Primality<T> {
    override fun T.isPrime(): Boolean = longIsPrime(with(integral) { toLong() })
}

// ── Companion instances ───────────────────────────────────────────────────────

private val intInstance: Primality<Int> by lazy { Primality.from(Int32) }
private val longInstance: Primality<Long> by lazy { Primality.from(Int64) }
private val uintInstance: Primality<UInt> by lazy {
    object : Primality<UInt> {
        // UInt.MAX_VALUE = 4294967295 < Long.MAX_VALUE; toLong() is always safe and correct.
        override fun UInt.isPrime(): Boolean = longIsPrime(toLong())
    }
}

val Primality.Companion.int: Primality<Int> get() = intInstance
val Primality.Companion.long: Primality<Long> get() = longInstance
val Primality.Companion.uint: Primality<UInt> get() = uintInstance
