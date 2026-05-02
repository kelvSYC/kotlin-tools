package com.kelvsyc.kotlin.core.traits.integral

/**
 * `Log2` is a composable trait providing integer base-2 logarithm operations on values of type [T].
 *
 * [floorLog2] returns the largest integer k such that 2^k ≤ the receiver.
 * [ceilLog2] returns the smallest integer k such that 2^k ≥ the receiver.
 *
 * Both operations require the receiver to be strictly positive; they throw [IllegalArgumentException]
 * if the value is zero or negative. Unlike [PowerOfTwo.ceilingPowerOfTwo], [ceilLog2] never
 * overflows: the result is always a non-negative [Int] regardless of the width of [T].
 *
 * Instances for all eight primitive integral types are available via the companion:
 * - Signed: [Companion.byte], [Companion.short], [Companion.int], [Companion.long]
 * - Unsigned: [Companion.ubyte], [Companion.ushort], [Companion.uint], [Companion.ulong]
 *
 * An instance can also be derived from a [SignedIntegral] or [UnsignedIntegral] via [Companion.from].
 */
interface Log2<T> {
    companion object

    fun T.floorLog2(): Int
    fun T.ceilLog2(): Int
}

/**
 * Returns a [Log2] instance derived from [integral] for a signed integral type.
 *
 * Positivity is tested via [SignedIntegral.toLong]. Both operations delegate to
 * [BitCollection.highestSetBit] and [BitCollection.countSetBits], which are available
 * through [SignedIntegral]'s [BitCollection] supertype.
 */
fun <T> Log2.Companion.from(integral: SignedIntegral<T>): Log2<T> = object : Log2<T> {
    override fun T.floorLog2(): Int {
        require(with(integral) { toLong() > 0L }) { "expected positive value" }
        return with(integral) { highestSetBit()!! }
    }

    override fun T.ceilLog2(): Int {
        require(with(integral) { toLong() > 0L }) { "expected positive value" }
        val floor = with(integral) { highestSetBit()!! }
        return if (with(integral) { countSetBits() == 1 }) floor else floor + 1
    }
}

/**
 * Returns a [Log2] instance derived from [integral] for an unsigned integral type.
 *
 * Positivity is tested via [UnsignedIntegral.toULong]. Both operations delegate to
 * [BitCollection.highestSetBit] and [BitCollection.countSetBits], which are available
 * through [UnsignedIntegral]'s [BitCollection] supertype.
 */
fun <T> Log2.Companion.from(integral: UnsignedIntegral<T>): Log2<T> = object : Log2<T> {
    override fun T.floorLog2(): Int {
        require(with(integral) { toULong() > 0uL }) { "expected positive value" }
        return with(integral) { highestSetBit()!! }
    }

    override fun T.ceilLog2(): Int {
        require(with(integral) { toULong() > 0uL }) { "expected positive value" }
        val floor = with(integral) { highestSetBit()!! }
        return if (with(integral) { countSetBits() == 1 }) floor else floor + 1
    }
}

private val byteInstance: Log2<Byte> by lazy { Log2.from(Int8) }
private val shortInstance: Log2<Short> by lazy { Log2.from(Int16) }
private val intInstance: Log2<Int> by lazy { Log2.from(Int32) }
private val longInstance: Log2<Long> by lazy { Log2.from(Int64) }
private val ubyteInstance: Log2<UByte> by lazy { Log2.from(UInt8) }
private val ushortInstance: Log2<UShort> by lazy { Log2.from(UInt16) }
private val uintInstance: Log2<UInt> by lazy { Log2.from(UInt32) }
private val ulongInstance: Log2<ULong> by lazy { Log2.from(UInt64) }

val Log2.Companion.byte: Log2<Byte> get() = byteInstance
val Log2.Companion.short: Log2<Short> get() = shortInstance
val Log2.Companion.int: Log2<Int> get() = intInstance
val Log2.Companion.long: Log2<Long> get() = longInstance
val Log2.Companion.ubyte: Log2<UByte> get() = ubyteInstance
val Log2.Companion.ushort: Log2<UShort> get() = ushortInstance
val Log2.Companion.uint: Log2<UInt> get() = uintInstance
val Log2.Companion.ulong: Log2<ULong> get() = ulongInstance
