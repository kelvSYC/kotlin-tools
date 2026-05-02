package com.kelvsyc.kotlin.core

// Modular multiplication a*b mod m using binary (double-and-add) method.
// Requires a < m and m <= Long.MAX_VALUE so that a+a and result+a never overflow ULong.
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

// Returns true if n passes the Miller-Rabin test for witness a.
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

/**
 * Returns `true` if this value is a prime number.
 *
 * Uses a deterministic Miller-Rabin test with witnesses {2, 7, 61}, which is correct for all
 * values in the [Int] range.
 */
val Int.isPrime: Boolean
    get() {
        if (this < 2) return false
        if (this < 4) return true
        if (this % 2 == 0 || this % 3 == 0) return false
        val n = toLong().toULong()
        return millerRabin(n, 2uL) && millerRabin(n, 7uL) && millerRabin(n, 61uL)
    }

/**
 * Returns `true` if this value is a prime number.
 *
 * Uses a deterministic Miller-Rabin test with witnesses {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37},
 * which is correct for all values in the [Long] range.
 */
val Long.isPrime: Boolean
    get() {
        if (this < 2L) return false
        if (this < 4L) return true
        if (this % 2L == 0L || this % 3L == 0L) return false
        val n = toULong()
        return millerRabin(n, 2uL) && millerRabin(n, 3uL) && millerRabin(n, 5uL) &&
            millerRabin(n, 7uL) && millerRabin(n, 11uL) && millerRabin(n, 13uL) &&
            millerRabin(n, 17uL) && millerRabin(n, 19uL) && millerRabin(n, 23uL) &&
            millerRabin(n, 29uL) && millerRabin(n, 31uL) && millerRabin(n, 37uL)
    }
