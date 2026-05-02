package com.kelvsyc.kotlin.core.traits.integral

// ── OverflowCheckedArithmetic instances ───────────────────────────────────────
//
// All arithmetic delegates to Math.addExact / subtractExact / multiplyExact / divideExact,
// which throw ArithmeticException on overflow. Math.divideExact requires Java 18+;
// Math.addExact / subtractExact / multiplyExact / negateExact require Java 8+;
// Math.absExact requires Java 15+. All are available on the JDK 25 toolchain.

private val checkedIntInstance: OverflowCheckedArithmetic<Int> = object : OverflowCheckedArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = Math.addExact(this, other)
    override fun Int.subtract(other: Int): Int = Math.subtractExact(this, other)
    override fun Int.multiply(other: Int): Int = Math.multiplyExact(this, other)
    override fun Int.divide(other: Int): Int = Math.divideExact(this, other)
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)
}

private val checkedLongInstance: OverflowCheckedArithmetic<Long> = object : OverflowCheckedArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = Math.addExact(this, other)
    override fun Long.subtract(other: Long): Long = Math.subtractExact(this, other)
    override fun Long.multiply(other: Long): Long = Math.multiplyExact(this, other)
    override fun Long.divide(other: Long): Long = Math.divideExact(this, other)
    override fun Long.rem(other: Long): Long = this % other
    override fun Long.compareTo(other: Long): Int = this.compareTo(other)
}

val OverflowCheckedArithmetic.Companion.int: OverflowCheckedArithmetic<Int>
    get() = checkedIntInstance

val OverflowCheckedArithmetic.Companion.long: OverflowCheckedArithmetic<Long>
    get() = checkedLongInstance

// ── OverflowCheckedSignedArithmetic instances ─────────────────────────────────

private val checkedSignedIntInstance: OverflowCheckedSignedArithmetic<Int> =
    object : OverflowCheckedSignedArithmetic<Int> {
        override val zero: Int get() = 0
        override val one: Int get() = 1

        override fun Int.add(other: Int): Int = Math.addExact(this, other)
        override fun Int.subtract(other: Int): Int = Math.subtractExact(this, other)
        override fun Int.multiply(other: Int): Int = Math.multiplyExact(this, other)
        override fun Int.divide(other: Int): Int = Math.divideExact(this, other)
        override fun Int.rem(other: Int): Int = this % other
        override fun Int.compareTo(other: Int): Int = this.compareTo(other)

        override fun Int.negate(): Int = Math.negateExact(this)
        override fun Int.abs(): Int = Math.absExact(this)
    }

private val checkedSignedLongInstance: OverflowCheckedSignedArithmetic<Long> =
    object : OverflowCheckedSignedArithmetic<Long> {
        override val zero: Long get() = 0L
        override val one: Long get() = 1L

        override fun Long.add(other: Long): Long = Math.addExact(this, other)
        override fun Long.subtract(other: Long): Long = Math.subtractExact(this, other)
        override fun Long.multiply(other: Long): Long = Math.multiplyExact(this, other)
        override fun Long.divide(other: Long): Long = Math.divideExact(this, other)
        override fun Long.rem(other: Long): Long = this % other
        override fun Long.compareTo(other: Long): Int = this.compareTo(other)

        override fun Long.negate(): Long = Math.negateExact(this)
        override fun Long.abs(): Long = Math.absExact(this)
    }

val OverflowCheckedSignedArithmetic.Companion.int: OverflowCheckedSignedArithmetic<Int>
    get() = checkedSignedIntInstance

val OverflowCheckedSignedArithmetic.Companion.long: OverflowCheckedSignedArithmetic<Long>
    get() = checkedSignedLongInstance
