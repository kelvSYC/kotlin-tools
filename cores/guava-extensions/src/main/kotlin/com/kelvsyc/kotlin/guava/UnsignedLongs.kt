package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedLong
import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.traits.integral.CeilDiv
import com.kelvsyc.kotlin.core.traits.integral.Gcd
import com.kelvsyc.kotlin.core.traits.integral.IntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.RoundingRightShift
import com.kelvsyc.kotlin.core.traits.integral.StickyRightShift
import com.kelvsyc.kotlin.core.traits.integral.UInt64
import com.kelvsyc.kotlin.core.traits.integral.from

operator fun UnsignedLong.div(other: UnsignedLong): UnsignedLong = dividedBy(other)
operator fun UnsignedLong.rem(other: UnsignedLong): UnsignedLong = mod(other)

fun ULong.toUnsignedLong(): UnsignedLong = UnsignedLong.fromLongBits(toLong())
fun UnsignedLong.toULong(): ULong = toLong().toULong()

val uLongToUnsignedLong: Converter<ULong, UnsignedLong> = Converter.of(
    forward = { it.toUnsignedLong() },
    backward = { it.toULong() }
)

private val unsignedLongInstance: UInt64<UnsignedLong> = object : UInt64<UnsignedLong> {
    override val zero: UnsignedLong get() = UnsignedLong.ZERO
    override val allSet: UnsignedLong get() = UnsignedLong.MAX_VALUE
    override val lsb: UnsignedLong get() = UnsignedLong.fromLongBits(1L)
    override val msb: UnsignedLong get() = UnsignedLong.fromLongBits(Long.MIN_VALUE)

    override fun UnsignedLong.countLeadingClearBits(): Int = toLong().countLeadingZeroBits()
    override fun UnsignedLong.countTrailingClearBits(): Int = toLong().countTrailingZeroBits()
    override fun UnsignedLong.countSetBits(): Int = toLong().countOneBits()
    override fun UnsignedLong.leftRotate(bitCount: Int): UnsignedLong = UnsignedLong.fromLongBits(toLong().rotateLeft(bitCount))
    override fun UnsignedLong.rightRotate(bitCount: Int): UnsignedLong = UnsignedLong.fromLongBits(toLong().rotateRight(bitCount))
    override fun UnsignedLong.leftShift(bits: Int): UnsignedLong = UnsignedLong.fromLongBits(toLong() shl bits)
    override fun UnsignedLong.logicalRightShift(bits: Int): UnsignedLong = UnsignedLong.fromLongBits(toLong() ushr bits)
    override fun UnsignedLong.bitwiseAnd(other: UnsignedLong): UnsignedLong = UnsignedLong.fromLongBits(toLong() and other.toLong())
    override fun UnsignedLong.bitwiseOr(other: UnsignedLong): UnsignedLong = UnsignedLong.fromLongBits(toLong() or other.toLong())
    override fun UnsignedLong.bitwiseXor(other: UnsignedLong): UnsignedLong = UnsignedLong.fromLongBits(toLong() xor other.toLong())
    override fun UnsignedLong.takeLowestSetBit(): UnsignedLong = UnsignedLong.fromLongBits(toLong().takeLowestOneBit())
    override fun UnsignedLong.takeHighestSetBit(): UnsignedLong = UnsignedLong.fromLongBits(toLong().takeHighestOneBit())
    override fun UnsignedLong.toULong(): ULong = toLong().toULong()
    override fun fromULong(value: ULong): UnsignedLong = UnsignedLong.fromLongBits(value.toLong())
}

val UInt64.Companion.unsignedLong: UInt64<UnsignedLong> get() = unsignedLongInstance

private val unsignedLongArithmeticInstance: IntegerArithmetic<UnsignedLong> = object : IntegerArithmetic<UnsignedLong> {
    override val zero: UnsignedLong get() = UnsignedLong.ZERO
    override val one: UnsignedLong get() = UnsignedLong.ONE
    override fun UnsignedLong.add(other: UnsignedLong): UnsignedLong = plus(other)
    override fun UnsignedLong.subtract(other: UnsignedLong): UnsignedLong = minus(other)
    override fun UnsignedLong.multiply(other: UnsignedLong): UnsignedLong = times(other)
    override fun UnsignedLong.divide(other: UnsignedLong): UnsignedLong = dividedBy(other)
    override fun UnsignedLong.rem(other: UnsignedLong): UnsignedLong = mod(other)
    override fun UnsignedLong.compareTo(other: UnsignedLong): Int = this.compareTo(other)
}

val IntegerArithmetic.Companion.unsignedLong: IntegerArithmetic<UnsignedLong> get() = unsignedLongArithmeticInstance

private val unsignedLongCeilDivInstance: CeilDiv<UnsignedLong> by lazy { CeilDiv.from(IntegerArithmetic.unsignedLong) }
private val unsignedLongGcdInstance: Gcd<UnsignedLong> by lazy { Gcd.from(IntegerArithmetic.unsignedLong) }
private val unsignedLongRoundingRightShiftInstance: RoundingRightShift<UnsignedLong> by lazy { RoundingRightShift.from(UInt64.unsignedLong, UInt64.unsignedLong, IntegerArithmetic.unsignedLong) }
private val unsignedLongStickyRightShiftInstance: StickyRightShift<UnsignedLong> by lazy { StickyRightShift.from(UInt64.unsignedLong, UInt64.unsignedLong) }

val CeilDiv.Companion.unsignedLong: CeilDiv<UnsignedLong> get() = unsignedLongCeilDivInstance
val Gcd.Companion.unsignedLong: Gcd<UnsignedLong> get() = unsignedLongGcdInstance
val RoundingRightShift.Companion.unsignedLong: RoundingRightShift<UnsignedLong> get() = unsignedLongRoundingRightShiftInstance
val StickyRightShift.Companion.unsignedLong: StickyRightShift<UnsignedLong> get() = unsignedLongStickyRightShiftInstance
