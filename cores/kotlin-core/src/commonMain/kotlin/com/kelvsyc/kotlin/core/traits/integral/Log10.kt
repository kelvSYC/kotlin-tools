@file:OptIn(ExperimentalUnsignedTypes::class)

package com.kelvsyc.kotlin.core.traits.integral

/**
 * `Log10` is a composable trait providing integer base-10 logarithm operations on values of type [T].
 *
 * [floorLog10] returns the largest integer k such that 10^k ≤ the receiver.
 * [ceilLog10] returns the smallest integer k such that 10^k ≥ the receiver.
 *
 * Both operations require the receiver to be strictly positive; they throw [IllegalArgumentException]
 * if the value is zero or negative. Like [Log2.ceilLog2], [ceilLog10] never overflows: the result is
 * always a non-negative [Int] regardless of the width of [T].
 *
 * Instances for all eight primitive integral types are available via the companion:
 * - Signed: [Companion.byte], [Companion.short], [Companion.int], [Companion.long]
 * - Unsigned: [Companion.ubyte], [Companion.ushort], [Companion.uint], [Companion.ulong]
 *
 * An instance can also be derived from a [SignedIntegral] or [UnsignedIntegral] via [Companion.from].
 */
interface Log10<T> {
    companion object

    fun T.floorLog10(): Int
    fun T.ceilLog10(): Int
}

// Largest power of 10 that fits in a Long (10^18); the table is indexed by exponent.
private val LONG_POWERS_OF_10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L,
    100_000_000L, 1_000_000_000L, 10_000_000_000L, 100_000_000_000L,
    1_000_000_000_000L, 10_000_000_000_000L, 100_000_000_000_000L,
    1_000_000_000_000_000L, 10_000_000_000_000_000L, 100_000_000_000_000_000L,
    1_000_000_000_000_000_000L,
)

// Extends to 10^19 for unsigned types whose max exceeds Long.MAX_VALUE.
private val ULONG_POWERS_OF_10 = ulongArrayOf(
    1uL, 10uL, 100uL, 1_000uL, 10_000uL, 100_000uL, 1_000_000uL, 10_000_000uL,
    100_000_000uL, 1_000_000_000uL, 10_000_000_000uL, 100_000_000_000uL,
    1_000_000_000_000uL, 10_000_000_000_000uL, 100_000_000_000_000uL,
    1_000_000_000_000_000uL, 10_000_000_000_000_000uL, 100_000_000_000_000_000uL,
    1_000_000_000_000_000_000uL, 10_000_000_000_000_000_000uL,
)

private fun floorLog10Long(n: Long): Int {
    var r = LONG_POWERS_OF_10.size - 1
    while (LONG_POWERS_OF_10[r] > n) r--
    return r
}

private fun floorLog10ULong(n: ULong): Int {
    var r = ULONG_POWERS_OF_10.size - 1
    while (ULONG_POWERS_OF_10[r] > n) r--
    return r
}

/**
 * Returns a [Log10] instance derived from [integral] for a signed integral type.
 *
 * Positivity is tested via [SignedIntegral.toLong]. Both operations use an exact powers-of-ten
 * lookup table, so there are no floating-point rounding concerns.
 */
fun <T> Log10.Companion.from(integral: SignedIntegral<T>): Log10<T> = object : Log10<T> {
    override fun T.floorLog10(): Int {
        val n = with(integral) { toLong() }
        require(n > 0L) { "expected positive value" }
        return floorLog10Long(n)
    }

    override fun T.ceilLog10(): Int {
        val n = with(integral) { toLong() }
        require(n > 0L) { "expected positive value" }
        val floor = floorLog10Long(n)
        return if (LONG_POWERS_OF_10[floor] == n) floor else floor + 1
    }
}

/**
 * Returns a [Log10] instance derived from [integral] for an unsigned integral type.
 *
 * Positivity is tested via [UnsignedIntegral.toULong]. Both operations use an exact powers-of-ten
 * lookup table that covers up to 10^19, encompassing [ULong.MAX_VALUE].
 */
fun <T> Log10.Companion.from(integral: UnsignedIntegral<T>): Log10<T> = object : Log10<T> {
    override fun T.floorLog10(): Int {
        val n = with(integral) { toULong() }
        require(n > 0uL) { "expected positive value" }
        return floorLog10ULong(n)
    }

    override fun T.ceilLog10(): Int {
        val n = with(integral) { toULong() }
        require(n > 0uL) { "expected positive value" }
        val floor = floorLog10ULong(n)
        return if (ULONG_POWERS_OF_10[floor] == n) floor else floor + 1
    }
}

private val byteInstance: Log10<Byte> by lazy { Log10.from(Int8) }
private val shortInstance: Log10<Short> by lazy { Log10.from(Int16) }
private val intInstance: Log10<Int> by lazy { Log10.from(Int32) }
private val longInstance: Log10<Long> by lazy { Log10.from(Int64) }
private val ubyteInstance: Log10<UByte> by lazy { Log10.from(UInt8) }
private val ushortInstance: Log10<UShort> by lazy { Log10.from(UInt16) }
private val uintInstance: Log10<UInt> by lazy { Log10.from(UInt32) }
private val ulongInstance: Log10<ULong> by lazy { Log10.from(UInt64) }

val Log10.Companion.byte: Log10<Byte> get() = byteInstance
val Log10.Companion.short: Log10<Short> get() = shortInstance
val Log10.Companion.int: Log10<Int> get() = intInstance
val Log10.Companion.long: Log10<Long> get() = longInstance
val Log10.Companion.ubyte: Log10<UByte> get() = ubyteInstance
val Log10.Companion.ushort: Log10<UShort> get() = ushortInstance
val Log10.Companion.uint: Log10<UInt> get() = uintInstance
val Log10.Companion.ulong: Log10<ULong> get() = ulongInstance
