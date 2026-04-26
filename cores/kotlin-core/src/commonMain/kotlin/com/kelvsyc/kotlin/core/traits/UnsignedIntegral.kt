package com.kelvsyc.kotlin.core.traits

/**
 * `UnsignedIntegral` is a trait type that contains metadata on the type [T] relating to unsigned integral numbers.
 */
interface UnsignedIntegral<T> : ValueEquality<T> {
    /**
     * The number of bits used to represent the unsigned integral number
     */
    val sizeBits: Int

    /**
     * The zero value.
     */
    val zero: T

    /**
     * Returns `true` if [other] represents the same value as this.
     *
     * The default uses `==`, which is correct for all standard unsigned types. Custom
     * implementations (e.g. a 128-bit type backed by two [Long]s, or a reverse-endian wrapper)
     * may override this if `==` does not reflect value equality for that type.
     */
    override fun T.isEqualTo(other: T): Boolean = this == other

    fun T.countLeadingZeroBits(): Int
    fun T.countTrailingZeroBits(): Int
    fun T.shl(n: Int): T
    fun T.shr(n: Int): T
    fun T.and(other: T): T
    fun T.or(other: T): T

    /**
     * Widens this value to a [ULong].
     */
    fun T.toULong(): ULong

    /**
     * Narrows [value] from [ULong] to [T], truncating high bits.
     */
    fun fromULong(value: ULong): T
}

/**
 * Trait type containing metadata on unsigned 16-bit integral numbers.
 */
interface UInt16<T> : UnsignedIntegral<T> {
    override val sizeBits: Int get() = 16

    companion object : UInt16<UShort> {
        override val zero: UShort get() = 0u.toUShort()

        // All operations route through Int rather than calling the stdlib UShort overloads directly.
        // Inside a companion that implements UnsignedIntegral<T>, member extension resolution gives
        // our own overrides priority over stdlib extensions with the same signature — so e.g.
        // `this.shr(n)` inside `override fun UShort.shr` would call itself, not the stdlib version.
        // Calling through Int uses a different receiver type, which is not subject to that dispatch.
        override fun UShort.countLeadingZeroBits(): Int = toInt().countLeadingZeroBits() - 16
        override fun UShort.countTrailingZeroBits(): Int = minOf(toInt().countTrailingZeroBits(), 16)
        override fun UShort.shl(n: Int): UShort = (toInt() shl n).toUShort()
        override fun UShort.shr(n: Int): UShort = (toInt() ushr n).toUShort()
        override fun UShort.and(other: UShort): UShort = (toInt() and other.toInt()).toUShort()
        override fun UShort.or(other: UShort): UShort = (toInt() or other.toInt()).toUShort()
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

        // See UInt16.Companion for an explanation of why operations route through Int.
        override fun UInt.countLeadingZeroBits(): Int = toInt().countLeadingZeroBits()
        override fun UInt.countTrailingZeroBits(): Int = toInt().countTrailingZeroBits()
        override fun UInt.shl(n: Int): UInt = (toInt() shl n).toUInt()
        override fun UInt.shr(n: Int): UInt = (toInt() ushr n).toUInt()
        override fun UInt.and(other: UInt): UInt = (toInt() and other.toInt()).toUInt()
        override fun UInt.or(other: UInt): UInt = (toInt() or other.toInt()).toUInt()
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

        // See UInt16.Companion for an explanation of why operations route through Long.
        override fun ULong.countLeadingZeroBits(): Int = toLong().countLeadingZeroBits()
        override fun ULong.countTrailingZeroBits(): Int = toLong().countTrailingZeroBits()
        override fun ULong.shl(n: Int): ULong = (toLong() shl n).toULong()
        override fun ULong.shr(n: Int): ULong = (toLong() ushr n).toULong()
        override fun ULong.and(other: ULong): ULong = (toLong() and other.toLong()).toULong()
        override fun ULong.or(other: ULong): ULong = (toLong() or other.toLong()).toULong()
        override fun ULong.toULong(): ULong = this
        override fun fromULong(value: ULong): ULong = value
    }
}
