package com.kelvsyc.kotlin.core.traits

/**
 * `BitShift` is a trait type providing a uniform interface for logical bit shift operations on values of type [T].
 */
interface BitShift<T> {
    companion object

    fun T.leftShift(bits: Int): T
    fun T.logicalRightShift(bits: Int): T
}

private val intInstance: BitShift<Int> = object : BitShift<Int> {
    override fun Int.leftShift(bits: Int): Int = this shl bits
    override fun Int.logicalRightShift(bits: Int): Int = this ushr bits
}

private val longInstance: BitShift<Long> = object : BitShift<Long> {
    override fun Long.leftShift(bits: Int): Long = this shl bits
    override fun Long.logicalRightShift(bits: Int): Long = this ushr bits
}

val BitShift.Companion.int: BitShift<Int> get() = intInstance
val BitShift.Companion.long: BitShift<Long> get() = longInstance
val BitShift.Companion.short: BitShift<Short> get() = Int16
val BitShift.Companion.ushort: BitShift<UShort> get() = UInt16
val BitShift.Companion.uint: BitShift<UInt> get() = UInt32
val BitShift.Companion.ulong: BitShift<ULong> get() = UInt64
