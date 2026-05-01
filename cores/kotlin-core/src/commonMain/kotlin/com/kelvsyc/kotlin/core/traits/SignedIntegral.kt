package com.kelvsyc.kotlin.core.traits

/**
 * `SignedIntegral` is a trait type that contains metadata on the type [T] relating to signed integral numbers.
 *
 * This trait extends [BitCollection] (which itself extends [Bitwise]), [BitShift], and [ArithmeticRightShift],
 * bundling the fixed-width bit structure, bitwise operations, logical and arithmetic shift operations that all
 * signed integer types share.
 *
 * The [allClear] value from [BitCollection] defaults to [zero], as for signed integers in two's-complement
 * representation the arithmetic zero and the all-zeros bit pattern are the same value.
 */
interface SignedIntegral<T> : BitCollection<T>, BitShift<T>, ArithmeticRightShift<T>, ValueEquality<T> {
    val zero: T

    override val allClear: T get() = zero

    fun T.toLong(): Long
    fun fromLong(value: Long): T

    override fun T.isEqualTo(other: T): Boolean = this == other
}

/**
 * Trait type containing metadata on signed 16-bit integral numbers.
 */
interface Int16<T> : SignedIntegral<T> {
    override val sizeBits: Int get() = 16

    companion object : Int16<Short> {
        override val zero: Short get() = 0.toShort()
        override val allSet: Short get() = (-1).toShort()
        override val lsb: Short get() = 1.toShort()
        override val msb: Short get() = Short.MIN_VALUE

        // Operations route through Int rather than calling stdlib UShort overloads directly.
        // Inside a companion that implements SignedIntegral<T>, member extension resolution gives
        // our own overrides priority over stdlib extensions with the same signature.
        // Routing through Int uses a different receiver type, avoiding that dispatch.
        //
        // Short.toInt() sign-extends; masking with 0xFFFF gives the zero-extended bit pattern
        // needed for correct leading-zero and popcount calculations on 16-bit values.
        override fun Short.countLeadingClearBits(): Int = (toInt() and 0xFFFF).countLeadingZeroBits() - 16
        override fun Short.countTrailingClearBits(): Int = minOf(toInt().countTrailingZeroBits(), 16)
        override fun Short.countSetBits(): Int = (toInt() and 0xFFFF).countOneBits()
        override fun Short.leftRotate(bitCount: Int): Short {
            val n = bitCount and 15
            val bits = toInt() and 0xFFFF
            return ((bits shl n) or (bits ushr (16 - n))).toShort()
        }
        override fun Short.rightRotate(bitCount: Int): Short {
            val n = bitCount and 15
            val bits = toInt() and 0xFFFF
            return ((bits ushr n) or (bits shl (16 - n))).toShort()
        }
        override fun Short.leftShift(bits: Int): Short = (toInt() shl bits).toShort()
        override fun Short.logicalRightShift(bits: Int): Short = ((toInt() and 0xFFFF) ushr bits).toShort()
        override fun Short.arithmeticRightShift(bits: Int): Short = (toInt() shr bits).toShort()
        override fun Short.bitwiseAnd(other: Short): Short = (toInt() and other.toInt()).toShort()
        override fun Short.bitwiseOr(other: Short): Short = (toInt() or other.toInt()).toShort()
        override fun Short.bitwiseXor(other: Short): Short = (toInt() xor other.toInt()).toShort()
        override fun Short.takeLowestSetBit(): Short = toInt().takeLowestOneBit().toShort()
        override fun Short.takeHighestSetBit(): Short = (toInt() and 0xFFFF).takeHighestOneBit().toShort()
        override fun Short.toLong(): Long = toInt().toLong()
        override fun fromLong(value: Long): Short = value.toShort()
    }
}

/**
 * Trait type containing metadata on signed 32-bit integral numbers.
 */
interface Int32<T> : SignedIntegral<T> {
    override val sizeBits: Int get() = 32

    companion object : Int32<Int> {
        override val zero: Int get() = 0
        override val allSet: Int get() = -1
        override val lsb: Int get() = 1
        override val msb: Int get() = Int.MIN_VALUE

        override fun Int.countLeadingClearBits(): Int = countLeadingZeroBits()
        override fun Int.countTrailingClearBits(): Int = countTrailingZeroBits()
        override fun Int.countSetBits(): Int = countOneBits()
        override fun Int.leftRotate(bitCount: Int): Int = rotateLeft(bitCount)
        override fun Int.rightRotate(bitCount: Int): Int = rotateRight(bitCount)
        override fun Int.leftShift(bits: Int): Int = this shl bits
        override fun Int.logicalRightShift(bits: Int): Int = this ushr bits
        override fun Int.arithmeticRightShift(bits: Int): Int = this shr bits
        override fun Int.bitwiseAnd(other: Int): Int = this and other
        override fun Int.bitwiseOr(other: Int): Int = this or other
        override fun Int.bitwiseXor(other: Int): Int = this xor other
        override fun Int.takeLowestSetBit(): Int = takeLowestOneBit()
        override fun Int.takeHighestSetBit(): Int = takeHighestOneBit()
        override fun Int.toLong(): Long = toLong()
        override fun fromLong(value: Long): Int = value.toInt()
    }
}

/**
 * Trait type containing metadata on signed 64-bit integral numbers.
 */
interface Int64<T> : SignedIntegral<T> {
    override val sizeBits: Int get() = 64

    companion object : Int64<Long> {
        override val zero: Long get() = 0L
        override val allSet: Long get() = -1L
        override val lsb: Long get() = 1L
        override val msb: Long get() = Long.MIN_VALUE

        override fun Long.countLeadingClearBits(): Int = countLeadingZeroBits()
        override fun Long.countTrailingClearBits(): Int = countTrailingZeroBits()
        override fun Long.countSetBits(): Int = countOneBits()
        override fun Long.leftRotate(bitCount: Int): Long = rotateLeft(bitCount)
        override fun Long.rightRotate(bitCount: Int): Long = rotateRight(bitCount)
        override fun Long.leftShift(bits: Int): Long = this shl bits
        override fun Long.logicalRightShift(bits: Int): Long = this ushr bits
        override fun Long.arithmeticRightShift(bits: Int): Long = this shr bits
        override fun Long.bitwiseAnd(other: Long): Long = this and other
        override fun Long.bitwiseOr(other: Long): Long = this or other
        override fun Long.bitwiseXor(other: Long): Long = this xor other
        override fun Long.takeLowestSetBit(): Long = takeLowestOneBit()
        override fun Long.takeHighestSetBit(): Long = takeHighestOneBit()
        override fun Long.toLong(): Long = this
        override fun fromLong(value: Long): Long = value
    }
}
