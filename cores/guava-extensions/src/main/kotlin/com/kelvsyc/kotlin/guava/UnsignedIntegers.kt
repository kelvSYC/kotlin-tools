package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedInteger
import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.traits.integral.CeilDiv
import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.IntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.PowerOfTwo
import com.kelvsyc.kotlin.core.traits.integral.RoundingRightShift
import com.kelvsyc.kotlin.core.traits.integral.StickyRightShift
import com.kelvsyc.kotlin.core.traits.integral.UInt32
import com.kelvsyc.kotlin.core.traits.integral.from

operator fun UnsignedInteger.div(other: UnsignedInteger): UnsignedInteger = dividedBy(other)
operator fun UnsignedInteger.rem(other: UnsignedInteger): UnsignedInteger = mod(other)

fun UInt.toUnsignedInteger(): UnsignedInteger = UnsignedInteger.fromIntBits(toInt())
fun UnsignedInteger.toUInt(): UInt = toInt().toUInt()

val uIntToUnsignedInteger: Converter<UInt, UnsignedInteger> = Converter.of(
    forward = { it.toUnsignedInteger() },
    backward = { it.toUInt() }
)

private val unsignedIntegerInstance: UInt32<UnsignedInteger> = object : UInt32<UnsignedInteger> {
    override val zero: UnsignedInteger get() = UnsignedInteger.ZERO
    override val allSet: UnsignedInteger get() = UnsignedInteger.MAX_VALUE
    override val lsb: UnsignedInteger get() = UnsignedInteger.fromIntBits(1)
    override val msb: UnsignedInteger get() = UnsignedInteger.fromIntBits(Int.MIN_VALUE)

    override fun UnsignedInteger.countLeadingClearBits(): Int = toInt().countLeadingZeroBits()
    override fun UnsignedInteger.countTrailingClearBits(): Int = toInt().countTrailingZeroBits()
    override fun UnsignedInteger.countSetBits(): Int = toInt().countOneBits()
    override fun UnsignedInteger.leftRotate(bitCount: Int): UnsignedInteger = UnsignedInteger.fromIntBits(toInt().rotateLeft(bitCount))
    override fun UnsignedInteger.rightRotate(bitCount: Int): UnsignedInteger = UnsignedInteger.fromIntBits(toInt().rotateRight(bitCount))
    override fun UnsignedInteger.leftShift(bits: Int): UnsignedInteger = UnsignedInteger.fromIntBits(toInt() shl bits)
    override fun UnsignedInteger.logicalRightShift(bits: Int): UnsignedInteger = UnsignedInteger.fromIntBits(toInt() ushr bits)
    override fun UnsignedInteger.bitwiseAnd(other: UnsignedInteger): UnsignedInteger = UnsignedInteger.fromIntBits(toInt() and other.toInt())
    override fun UnsignedInteger.bitwiseOr(other: UnsignedInteger): UnsignedInteger = UnsignedInteger.fromIntBits(toInt() or other.toInt())
    override fun UnsignedInteger.bitwiseXor(other: UnsignedInteger): UnsignedInteger = UnsignedInteger.fromIntBits(toInt() xor other.toInt())
    override fun UnsignedInteger.takeLowestSetBit(): UnsignedInteger = UnsignedInteger.fromIntBits(toInt().takeLowestOneBit())
    override fun UnsignedInteger.takeHighestSetBit(): UnsignedInteger = UnsignedInteger.fromIntBits(toInt().takeHighestOneBit())
    override fun UnsignedInteger.toULong(): ULong = toLong().toULong()
    override fun fromULong(value: ULong): UnsignedInteger = UnsignedInteger.fromIntBits(value.toInt())
}

val UInt32.Companion.unsignedInteger: UInt32<UnsignedInteger> get() = unsignedIntegerInstance

private val unsignedIntegerArithmeticInstance: IntegerArithmetic<UnsignedInteger> = object : IntegerArithmetic<UnsignedInteger> {
    override val zero: UnsignedInteger get() = UnsignedInteger.ZERO
    override val one: UnsignedInteger get() = UnsignedInteger.ONE
    override fun UnsignedInteger.add(other: UnsignedInteger): UnsignedInteger = plus(other)
    override fun UnsignedInteger.subtract(other: UnsignedInteger): UnsignedInteger = minus(other)
    override fun UnsignedInteger.multiply(other: UnsignedInteger): UnsignedInteger = times(other)
    override fun UnsignedInteger.divide(other: UnsignedInteger): UnsignedInteger = dividedBy(other)
    override fun UnsignedInteger.rem(other: UnsignedInteger): UnsignedInteger = mod(other)
    override fun UnsignedInteger.compareTo(other: UnsignedInteger): Int = this.compareTo(other)
}

val IntegerArithmetic.Companion.unsignedInteger: IntegerArithmetic<UnsignedInteger> get() = unsignedIntegerArithmeticInstance

private val unsignedIntegerCeilDivInstance: CeilDiv<UnsignedInteger> by lazy { CeilDiv.from(IntegerArithmetic.unsignedInteger) }
private val unsignedIntegerGcdInstance: Gcd<UnsignedInteger> by lazy { Gcd.from(IntegerArithmetic.unsignedInteger) }
private val unsignedIntegerRoundingRightShiftInstance: RoundingRightShift<UnsignedInteger> by lazy { RoundingRightShift.from(UInt32.unsignedInteger, UInt32.unsignedInteger, IntegerArithmetic.unsignedInteger) }
private val unsignedIntegerStickyRightShiftInstance: StickyRightShift<UnsignedInteger> by lazy { StickyRightShift.from(UInt32.unsignedInteger, UInt32.unsignedInteger) }

private val unsignedIntegerPowerOfTwoInstance: PowerOfTwo<UnsignedInteger> by lazy { PowerOfTwo.from(UInt32.unsignedInteger) }

val CeilDiv.Companion.unsignedInteger: CeilDiv<UnsignedInteger> get() = unsignedIntegerCeilDivInstance
val Gcd.Companion.unsignedInteger: Gcd<UnsignedInteger> get() = unsignedIntegerGcdInstance
val PowerOfTwo.Companion.unsignedInteger: PowerOfTwo<UnsignedInteger> get() = unsignedIntegerPowerOfTwoInstance
val RoundingRightShift.Companion.unsignedInteger: RoundingRightShift<UnsignedInteger> get() = unsignedIntegerRoundingRightShiftInstance
val StickyRightShift.Companion.unsignedInteger: StickyRightShift<UnsignedInteger> get() = unsignedIntegerStickyRightShiftInstance
