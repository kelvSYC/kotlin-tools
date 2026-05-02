package com.kelvsyc.kotlin.core.traits.integral

/**
 * `BitCollection` is a trait denoting that a type can be treated as a fixed-size collection of bits.
 *
 * `BitCollection` extends [Bitwise], as any type with a known fixed-width bit structure supports the full
 * set of bitwise operations. The [invert] operation has a default implementation derived from [allSet]:
 * inverting is equivalent to XOR-ing with the all-ones value.
 */
interface BitCollection<T> : Bitwise<T> {
    val sizeBits: Int

    val allSet: T
    val allClear: T
    val lsb: T
    val msb: T

    fun T.leftRotate(bitCount: Int): T
    fun T.rightRotate(bitCount: Int): T

    fun T.countLeadingClearBits(): Int
    fun T.countTrailingClearBits(): Int
    fun T.countSetBits(): Int
    fun T.lowestSetBit(): Int? = countTrailingClearBits().let { if (it == sizeBits) null else it }
    fun T.highestSetBit(): Int? = countLeadingClearBits().let { if (it == sizeBits) null else sizeBits - it - 1 }
    fun T.takeLowestSetBit(): T
    fun T.takeHighestSetBit(): T

    override fun T.invert(): T = bitwiseXor(allSet)

    companion object
}

private val intInstance: BitCollection<Int> = object : BitCollection<Int> {
    override val sizeBits: Int get() = Int.SIZE_BITS
    override val allSet: Int get() = -1
    override val allClear: Int get() = 0
    override val lsb: Int get() = 1
    override val msb: Int get() = Int.MIN_VALUE

    override fun Int.bitwiseAnd(other: Int): Int = this and other
    override fun Int.bitwiseOr(other: Int): Int = this or other
    override fun Int.bitwiseXor(other: Int): Int = this xor other

    override fun Int.leftRotate(bitCount: Int): Int = rotateLeft(bitCount)
    override fun Int.rightRotate(bitCount: Int): Int = rotateRight(bitCount)
    override fun Int.countLeadingClearBits(): Int = countLeadingZeroBits()
    override fun Int.countTrailingClearBits(): Int = countTrailingZeroBits()
    override fun Int.countSetBits(): Int = countOneBits()
    override fun Int.takeLowestSetBit(): Int = takeLowestOneBit()
    override fun Int.takeHighestSetBit(): Int = takeHighestOneBit()
}

private val longInstance: BitCollection<Long> = object : BitCollection<Long> {
    override val sizeBits: Int get() = Long.SIZE_BITS
    override val allSet: Long get() = -1L
    override val allClear: Long get() = 0L
    override val lsb: Long get() = 1L
    override val msb: Long get() = Long.MIN_VALUE

    override fun Long.bitwiseAnd(other: Long): Long = this and other
    override fun Long.bitwiseOr(other: Long): Long = this or other
    override fun Long.bitwiseXor(other: Long): Long = this xor other

    override fun Long.leftRotate(bitCount: Int): Long = rotateLeft(bitCount)
    override fun Long.rightRotate(bitCount: Int): Long = rotateRight(bitCount)
    override fun Long.countLeadingClearBits(): Int = countLeadingZeroBits()
    override fun Long.countTrailingClearBits(): Int = countTrailingZeroBits()
    override fun Long.countSetBits(): Int = countOneBits()
    override fun Long.takeLowestSetBit(): Long = takeLowestOneBit()
    override fun Long.takeHighestSetBit(): Long = takeHighestOneBit()
}

val BitCollection.Companion.int: BitCollection<Int> get() = intInstance
val BitCollection.Companion.long: BitCollection<Long> get() = longInstance
val BitCollection.Companion.byte: BitCollection<Byte> get() = Int8
val BitCollection.Companion.ubyte: BitCollection<UByte> get() = UInt8
val BitCollection.Companion.short: BitCollection<Short> get() = Int16
val BitCollection.Companion.ushort: BitCollection<UShort> get() = UInt16
val BitCollection.Companion.uint: BitCollection<UInt> get() = UInt32
val BitCollection.Companion.ulong: BitCollection<ULong> get() = UInt64
