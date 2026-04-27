package com.kelvsyc.kotlin.core.traits

/**
 * `Bitwise` is a trait type providing a uniform interface for bitwise operations on values of type [T].
 *
 * All operations are size-independent - that is, they make no assumptions on the number of bits in [T].
 */
interface Bitwise<T> {
    companion object

    fun T.bitwiseAnd(other: T): T
    fun T.bitwiseOr(other: T): T
    fun T.bitwiseXor(other: T): T
    fun T.invert(): T
}

private val intInstance: Bitwise<Int> = object : Bitwise<Int> {
    override fun Int.bitwiseAnd(other: Int): Int = this and other
    override fun Int.bitwiseOr(other: Int): Int = this or other
    override fun Int.bitwiseXor(other: Int): Int = this xor other
    override fun Int.invert(): Int = this.inv()
}

private val longInstance: Bitwise<Long> = object : Bitwise<Long> {
    override fun Long.bitwiseAnd(other: Long): Long = this and other
    override fun Long.bitwiseOr(other: Long): Long = this or other
    override fun Long.bitwiseXor(other: Long): Long = this xor other
    override fun Long.invert(): Long = this.inv()
}

val Bitwise.Companion.int: Bitwise<Int> get() = intInstance
val Bitwise.Companion.long: Bitwise<Long> get() = longInstance
val Bitwise.Companion.ushort: Bitwise<UShort> get() = UInt16
val Bitwise.Companion.uint: Bitwise<UInt> get() = UInt32
val Bitwise.Companion.ulong: Bitwise<ULong> get() = UInt64
