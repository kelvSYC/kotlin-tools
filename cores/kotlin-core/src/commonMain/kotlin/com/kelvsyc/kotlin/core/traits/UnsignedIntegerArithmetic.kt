package com.kelvsyc.kotlin.core.traits

// ── IntegerArithmetic instances for unsigned types ────────────────────────────
//
// UByte and UShort: Kotlin arithmetic operators return UInt (widening), so results
// are narrowed back with toUByte()/toUShort(). UInt and ULong are native.
//
// Division and rem: Kotlin's / and % operators on unsigned types perform unsigned
// (non-negative) division and throw ArithmeticException on a zero divisor.

private val ubyteInstance: IntegerArithmetic<UByte> = object : IntegerArithmetic<UByte> {
    override val zero: UByte get() = 0u.toUByte()
    override val one: UByte get() = 1u.toUByte()
    override fun UByte.add(other: UByte): UByte = (this + other).toUByte()
    override fun UByte.subtract(other: UByte): UByte = (this - other).toUByte()
    override fun UByte.multiply(other: UByte): UByte = (this * other).toUByte()
    override fun UByte.divide(other: UByte): UByte = (this / other).toUByte()
    override fun UByte.rem(other: UByte): UByte = (this % other).toUByte()
    override fun UByte.compareTo(other: UByte): Int = this.compareTo(other)
}

private val ushortInstance: IntegerArithmetic<UShort> = object : IntegerArithmetic<UShort> {
    override val zero: UShort get() = 0u.toUShort()
    override val one: UShort get() = 1u.toUShort()
    override fun UShort.add(other: UShort): UShort = (this + other).toUShort()
    override fun UShort.subtract(other: UShort): UShort = (this - other).toUShort()
    override fun UShort.multiply(other: UShort): UShort = (this * other).toUShort()
    override fun UShort.divide(other: UShort): UShort = (this / other).toUShort()
    override fun UShort.rem(other: UShort): UShort = (this % other).toUShort()
    override fun UShort.compareTo(other: UShort): Int = this.compareTo(other)
}

private val uintInstance: IntegerArithmetic<UInt> = object : IntegerArithmetic<UInt> {
    override val zero: UInt get() = 0u
    override val one: UInt get() = 1u
    override fun UInt.add(other: UInt): UInt = this + other
    override fun UInt.subtract(other: UInt): UInt = this - other
    override fun UInt.multiply(other: UInt): UInt = this * other
    override fun UInt.divide(other: UInt): UInt = this / other
    override fun UInt.rem(other: UInt): UInt = this % other
    override fun UInt.compareTo(other: UInt): Int = this.compareTo(other)
}

private val ulongInstance: IntegerArithmetic<ULong> = object : IntegerArithmetic<ULong> {
    override val zero: ULong get() = 0uL
    override val one: ULong get() = 1uL
    override fun ULong.add(other: ULong): ULong = this + other
    override fun ULong.subtract(other: ULong): ULong = this - other
    override fun ULong.multiply(other: ULong): ULong = this * other
    override fun ULong.divide(other: ULong): ULong = this / other
    override fun ULong.rem(other: ULong): ULong = this % other
    override fun ULong.compareTo(other: ULong): Int = this.compareTo(other)
}

val IntegerArithmetic.Companion.ubyte: IntegerArithmetic<UByte> get() = ubyteInstance
val IntegerArithmetic.Companion.ushort: IntegerArithmetic<UShort> get() = ushortInstance
val IntegerArithmetic.Companion.uint: IntegerArithmetic<UInt> get() = uintInstance
val IntegerArithmetic.Companion.ulong: IntegerArithmetic<ULong> get() = ulongInstance
