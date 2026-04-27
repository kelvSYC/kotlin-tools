package com.kelvsyc.kotlin.core.traits

/**
 * `UnsignedIntegral` is a trait type that contains metadata on the type [T] relating to unsigned integral numbers.
 *
 * This trait extends [BitCollection] (which itself extends [Bitwise]) and [BitShift], bundling the fixed-width
 * bit structure, bitwise operations, and logical shift operations that all unsigned integer types share.
 *
 * The [allClear] value from [BitCollection] defaults to [zero], as for unsigned integers the arithmetic zero
 * and the all-zeros bit pattern are the same value.
 */
interface UnsignedIntegral<T> : BitCollection<T>, BitShift<T>, ValueEquality<T> {
    val zero: T

    override val allClear: T get() = zero

    fun T.toULong(): ULong
    fun fromULong(value: ULong): T

    override fun T.isEqualTo(other: T): Boolean = this == other
}

/**
 * Trait type containing metadata on unsigned 16-bit integral numbers.
 */
interface UInt16<T> : UnsignedIntegral<T> {
    override val sizeBits: Int get() = 16

    companion object : UInt16<UShort> {
        override val zero: UShort get() = 0u.toUShort()
        override val allSet: UShort get() = UShort.MAX_VALUE
        override val lsb: UShort get() = 1u.toUShort()
        override val msb: UShort get() = 0x8000u.toUShort()

        // All operations route through Int rather than calling the stdlib UShort overloads directly.
        // Inside a companion that implements UnsignedIntegral<T>, member extension resolution gives
        // our own overrides priority over stdlib extensions with the same signature — so e.g.
        // `this.countLeadingClearBits()` inside the override would call itself, not the stdlib version.
        // Calling through Int uses a different receiver type, which is not subject to that dispatch.
        override fun UShort.countLeadingClearBits(): Int = toInt().countLeadingZeroBits() - 16
        override fun UShort.countTrailingClearBits(): Int = minOf(toInt().countTrailingZeroBits(), 16)
        override fun UShort.countSetBits(): Int = toInt().countOneBits()
        override fun UShort.leftRotate(bitCount: Int): UShort {
            val n = bitCount and 15
            return ((toInt() shl n) or (toInt() ushr (16 - n))).toUShort()
        }
        override fun UShort.rightRotate(bitCount: Int): UShort {
            val n = bitCount and 15
            return ((toInt() ushr n) or (toInt() shl (16 - n))).toUShort()
        }
        override fun UShort.leftShift(bits: Int): UShort = (toInt() shl bits).toUShort()
        override fun UShort.logicalRightShift(bits: Int): UShort = (toInt() ushr bits).toUShort()
        override fun UShort.bitwiseAnd(other: UShort): UShort = (toInt() and other.toInt()).toUShort()
        override fun UShort.bitwiseOr(other: UShort): UShort = (toInt() or other.toInt()).toUShort()
        override fun UShort.bitwiseXor(other: UShort): UShort = (toInt() xor other.toInt()).toUShort()
        override fun UShort.takeLowestSetBit(): UShort = (toInt().takeLowestOneBit()).toUShort()
        override fun UShort.takeHighestSetBit(): UShort = (toInt().takeHighestOneBit()).toUShort()
        override fun UShort.toULong(): ULong = toLong().toULong()
        override fun fromULong(value: ULong): UShort = value.toUShort()
    }
}

/**
 * Trait type containing metadata on unsigned 32-bit integral numbers.
 */
interface UInt32<T> : UnsignedIntegral<T> {
    override val sizeBits: Int get() = 32

    companion object : UInt32<UInt> {
        override val zero: UInt get() = 0u
        override val allSet: UInt get() = UInt.MAX_VALUE
        override val lsb: UInt get() = 1u
        override val msb: UInt get() = 0x80000000u

        // See UInt16.Companion for an explanation of why operations route through Int.
        override fun UInt.countLeadingClearBits(): Int = toInt().countLeadingZeroBits()
        override fun UInt.countTrailingClearBits(): Int = toInt().countTrailingZeroBits()
        override fun UInt.countSetBits(): Int = toInt().countOneBits()
        override fun UInt.leftRotate(bitCount: Int): UInt = (toInt().rotateLeft(bitCount)).toUInt()
        override fun UInt.rightRotate(bitCount: Int): UInt = (toInt().rotateRight(bitCount)).toUInt()
        override fun UInt.leftShift(bits: Int): UInt = (toInt() shl bits).toUInt()
        override fun UInt.logicalRightShift(bits: Int): UInt = (toInt() ushr bits).toUInt()
        override fun UInt.bitwiseAnd(other: UInt): UInt = (toInt() and other.toInt()).toUInt()
        override fun UInt.bitwiseOr(other: UInt): UInt = (toInt() or other.toInt()).toUInt()
        override fun UInt.bitwiseXor(other: UInt): UInt = (toInt() xor other.toInt()).toUInt()
        override fun UInt.takeLowestSetBit(): UInt = (toInt().takeLowestOneBit()).toUInt()
        override fun UInt.takeHighestSetBit(): UInt = (toInt().takeHighestOneBit()).toUInt()
        override fun UInt.toULong(): ULong = toLong().toULong()
        override fun fromULong(value: ULong): UInt = value.toUInt()
    }
}

/**
 * Trait type containing metadata on unsigned 64-bit integral numbers.
 */
interface UInt64<T> : UnsignedIntegral<T> {
    override val sizeBits: Int get() = 64

    companion object : UInt64<ULong> {
        override val zero: ULong get() = 0uL
        override val allSet: ULong get() = ULong.MAX_VALUE
        override val lsb: ULong get() = 1uL
        override val msb: ULong get() = 0x8000000000000000uL

        // See UInt16.Companion for an explanation of why operations route through Long.
        override fun ULong.countLeadingClearBits(): Int = toLong().countLeadingZeroBits()
        override fun ULong.countTrailingClearBits(): Int = toLong().countTrailingZeroBits()
        override fun ULong.countSetBits(): Int = toLong().countOneBits()
        override fun ULong.leftRotate(bitCount: Int): ULong = (toLong().rotateLeft(bitCount)).toULong()
        override fun ULong.rightRotate(bitCount: Int): ULong = (toLong().rotateRight(bitCount)).toULong()
        override fun ULong.leftShift(bits: Int): ULong = (toLong() shl bits).toULong()
        override fun ULong.logicalRightShift(bits: Int): ULong = (toLong() ushr bits).toULong()
        override fun ULong.bitwiseAnd(other: ULong): ULong = (toLong() and other.toLong()).toULong()
        override fun ULong.bitwiseOr(other: ULong): ULong = (toLong() or other.toLong()).toULong()
        override fun ULong.bitwiseXor(other: ULong): ULong = (toLong() xor other.toLong()).toULong()
        override fun ULong.takeLowestSetBit(): ULong = (toLong().takeLowestOneBit()).toULong()
        override fun ULong.takeHighestSetBit(): ULong = (toLong().takeHighestOneBit()).toULong()
        override fun ULong.toULong(): ULong = this
        override fun fromULong(value: ULong): ULong = value
    }
}
