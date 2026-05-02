package com.kelvsyc.kotlin.core.traits.integral

/**
 * `PowerOfTwo` is a composable trait providing floor and ceiling power-of-two operations on values of type [T].
 *
 * [floorPowerOfTwo] returns the largest power of two less than or equal to the receiver.
 * [ceilingPowerOfTwo] returns the smallest power of two greater than or equal to the receiver.
 * [isPowerOfTwo] returns `true` if the receiver is a positive exact power of two.
 *
 * Both rounding operations require the receiver to be strictly positive; they throw [IllegalArgumentException]
 * if the value is zero or negative.  [ceilingPowerOfTwo] additionally throws [ArithmeticException] when the
 * mathematical result exceeds the representable range of [T] (e.g. values above 2^30 for [Int]).
 *
 * Instances for all eight primitive integral types are available via the companion:
 * - Signed: [Companion.byte], [Companion.short], [Companion.int], [Companion.long]
 * - Unsigned: [Companion.ubyte], [Companion.ushort], [Companion.uint], [Companion.ulong]
 *
 * An instance can also be derived from a [SignedIntegral] or [UnsignedIntegral] via [Companion.from].
 */
interface PowerOfTwo<T> {
    companion object

    fun T.isPowerOfTwo(): Boolean
    fun T.floorPowerOfTwo(): T
    fun T.ceilingPowerOfTwo(): T
}

/**
 * Returns a [PowerOfTwo] instance derived from [integral] for a signed integral type.
 *
 * Positivity is tested via [SignedIntegral.toLong].  Overflow detection for [PowerOfTwo.ceilingPowerOfTwo]
 * is performed by checking whether the shifted result is negative (i.e. wrapped into the sign bit).
 */
fun <T> PowerOfTwo.Companion.from(integral: SignedIntegral<T>): PowerOfTwo<T> = object : PowerOfTwo<T> {
    override fun T.isPowerOfTwo(): Boolean =
        with(integral) { toLong() > 0L && countSetBits() == 1 }

    override fun T.floorPowerOfTwo(): T {
        require(with(integral) { toLong() > 0L }) { "expected positive value" }
        return with(integral) { takeHighestSetBit() }
    }

    override fun T.ceilingPowerOfTwo(): T {
        require(with(integral) { toLong() > 0L }) { "expected positive value" }
        if (isPowerOfTwo()) return this
        val result = with(integral) { takeHighestSetBit().leftShift(1) }
        if (with(integral) { result.toLong() < 0L }) throw ArithmeticException("overflow")
        return result
    }
}

/**
 * Returns a [PowerOfTwo] instance derived from [integral] for an unsigned integral type.
 *
 * Positivity is tested via [UnsignedIntegral.toULong].  Overflow detection for [PowerOfTwo.ceilingPowerOfTwo]
 * is performed by checking whether the shifted result wrapped to zero.
 */
fun <T> PowerOfTwo.Companion.from(integral: UnsignedIntegral<T>): PowerOfTwo<T> = object : PowerOfTwo<T> {
    override fun T.isPowerOfTwo(): Boolean =
        with(integral) { toULong() > 0uL && countSetBits() == 1 }

    override fun T.floorPowerOfTwo(): T {
        require(with(integral) { toULong() > 0uL }) { "expected positive value" }
        return with(integral) { takeHighestSetBit() }
    }

    override fun T.ceilingPowerOfTwo(): T {
        require(with(integral) { toULong() > 0uL }) { "expected positive value" }
        if (isPowerOfTwo()) return this
        val result = with(integral) { takeHighestSetBit().leftShift(1) }
        if (with(integral) { result.toULong() == 0uL }) throw ArithmeticException("overflow")
        return result
    }
}

private val byteInstance: PowerOfTwo<Byte> by lazy { PowerOfTwo.from(Int8) }
private val shortInstance: PowerOfTwo<Short> by lazy { PowerOfTwo.from(Int16) }
private val intInstance: PowerOfTwo<Int> by lazy { PowerOfTwo.from(Int32) }
private val longInstance: PowerOfTwo<Long> by lazy { PowerOfTwo.from(Int64) }
private val ubyteInstance: PowerOfTwo<UByte> by lazy { PowerOfTwo.from(UInt8) }
private val ushortInstance: PowerOfTwo<UShort> by lazy { PowerOfTwo.from(UInt16) }
private val uintInstance: PowerOfTwo<UInt> by lazy { PowerOfTwo.from(UInt32) }
private val ulongInstance: PowerOfTwo<ULong> by lazy { PowerOfTwo.from(UInt64) }

val PowerOfTwo.Companion.byte: PowerOfTwo<Byte> get() = byteInstance
val PowerOfTwo.Companion.short: PowerOfTwo<Short> get() = shortInstance
val PowerOfTwo.Companion.int: PowerOfTwo<Int> get() = intInstance
val PowerOfTwo.Companion.long: PowerOfTwo<Long> get() = longInstance
val PowerOfTwo.Companion.ubyte: PowerOfTwo<UByte> get() = ubyteInstance
val PowerOfTwo.Companion.ushort: PowerOfTwo<UShort> get() = ushortInstance
val PowerOfTwo.Companion.uint: PowerOfTwo<UInt> get() = uintInstance
val PowerOfTwo.Companion.ulong: PowerOfTwo<ULong> get() = ulongInstance
