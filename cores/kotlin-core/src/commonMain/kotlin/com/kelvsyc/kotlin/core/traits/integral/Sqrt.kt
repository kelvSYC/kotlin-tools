package com.kelvsyc.kotlin.core.traits.integral

import kotlin.math.sqrt

/**
 * `Sqrt` is a composable trait providing integer square root operations on values of type [T].
 *
 * [floorSqrt] returns the largest integer k such that k² ≤ the receiver.
 * [ceilSqrt] returns the smallest integer k such that k² ≥ the receiver.
 *
 * Both operations require the receiver to be non-negative; they throw [IllegalArgumentException]
 * for negative inputs. [ceilSqrt] never overflows: the result always fits in [T] for any valid input.
 *
 * Instances for all eight primitive integral types are available via the companion:
 * - Signed: [Companion.byte], [Companion.short], [Companion.int], [Companion.long]
 * - Unsigned: [Companion.ubyte], [Companion.ushort], [Companion.uint], [Companion.ulong]
 *
 * An instance can also be derived from a [SignedIntegral] or [UnsignedIntegral] via [Companion.from].
 */
interface Sqrt<T> {
    companion object

    fun T.floorSqrt(): T
    fun T.ceilSqrt(): T
}

// Integer square root for non-negative Long values.
// Result k satisfies k*k <= n < (k+1)*(k+1).
// Intermediate squared comparisons are done in ULong to avoid Long overflow near Long.MAX_VALUE
// (the floor sqrt of Long.MAX_VALUE is 3037000499, and 3037000500^2 exceeds Long.MAX_VALUE).
private fun isqrtLong(n: Long): Long {
    if (n == 0L) return 0L
    var k = sqrt(n.toDouble()).toLong().coerceAtLeast(1L)
    val nu = n.toULong()
    while (k.toULong() * k.toULong() > nu) k--
    while ((k + 1L).toULong() * (k + 1L).toULong() <= nu) k++
    return k
}

// Integer square root for ULong values.
// The result always fits in the range [0, 2^32-1], so k*k never overflows ULong
// (the maximum is (2^32-1)^2 = 2^64 - 2^33 + 1 < ULong.MAX_VALUE).
private fun isqrtULong(n: ULong): ULong {
    if (n == 0uL) return 0uL
    var k = sqrt(n.toDouble()).toULong().coerceAtMost(4294967295uL)
    while (k > 0uL && k * k > n) k--
    // Guard: (k+1)^2 overflows when k == 2^32-1; the result can't be larger anyway.
    while (k < 4294967295uL && (k + 1uL) * (k + 1uL) <= n) k++
    return k
}

/**
 * Returns a [Sqrt] instance derived from [integral] for a signed integral type.
 *
 * Non-negativity is tested via [SignedIntegral.toLong]. The floor and ceiling computations run in
 * Long and ULong arithmetic to avoid overflow, then convert back to [T] via [SignedIntegral.fromLong].
 */
fun <T> Sqrt.Companion.from(integral: SignedIntegral<T>): Sqrt<T> = object : Sqrt<T> {
    override fun T.floorSqrt(): T {
        val n = with(integral) { toLong() }
        require(n >= 0L) { "expected non-negative value" }
        return with(integral) { fromLong(isqrtLong(n)) }
    }

    override fun T.ceilSqrt(): T {
        val n = with(integral) { toLong() }
        require(n >= 0L) { "expected non-negative value" }
        val k = isqrtLong(n)
        return with(integral) { fromLong(if (k * k == n) k else k + 1L) }
    }
}

/**
 * Returns a [Sqrt] instance derived from [integral] for an unsigned integral type.
 *
 * All unsigned values are non-negative by definition, so no precondition check is required.
 * Computations run in ULong arithmetic and convert back via [UnsignedIntegral.fromULong].
 */
fun <T> Sqrt.Companion.from(integral: UnsignedIntegral<T>): Sqrt<T> = object : Sqrt<T> {
    override fun T.floorSqrt(): T {
        val n = with(integral) { toULong() }
        return with(integral) { fromULong(isqrtULong(n)) }
    }

    override fun T.ceilSqrt(): T {
        val n = with(integral) { toULong() }
        val k = isqrtULong(n)
        return with(integral) { fromULong(if (k * k == n) k else k + 1uL) }
    }
}

private val byteInstance: Sqrt<Byte> by lazy { Sqrt.from(Int8) }
private val shortInstance: Sqrt<Short> by lazy { Sqrt.from(Int16) }
private val intInstance: Sqrt<Int> by lazy { Sqrt.from(Int32) }
private val longInstance: Sqrt<Long> by lazy { Sqrt.from(Int64) }
private val ubyteInstance: Sqrt<UByte> by lazy { Sqrt.from(UInt8) }
private val ushortInstance: Sqrt<UShort> by lazy { Sqrt.from(UInt16) }
private val uintInstance: Sqrt<UInt> by lazy { Sqrt.from(UInt32) }
private val ulongInstance: Sqrt<ULong> by lazy { Sqrt.from(UInt64) }

val Sqrt.Companion.byte: Sqrt<Byte> get() = byteInstance
val Sqrt.Companion.short: Sqrt<Short> get() = shortInstance
val Sqrt.Companion.int: Sqrt<Int> get() = intInstance
val Sqrt.Companion.long: Sqrt<Long> get() = longInstance
val Sqrt.Companion.ubyte: Sqrt<UByte> get() = ubyteInstance
val Sqrt.Companion.ushort: Sqrt<UShort> get() = ushortInstance
val Sqrt.Companion.uint: Sqrt<UInt> get() = uintInstance
val Sqrt.Companion.ulong: Sqrt<ULong> get() = ulongInstance
