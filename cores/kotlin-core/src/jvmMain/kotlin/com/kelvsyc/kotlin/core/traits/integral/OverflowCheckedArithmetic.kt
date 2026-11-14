package com.kelvsyc.kotlin.core.traits.integral

// ── OverflowCheckedArithmetic instances ───────────────────────────────────────
//
// All arithmetic delegates to Math.addExact / subtractExact / multiplyExact / negateExact (Java 8+)
// and Math.absExact (Java 15+), which throw ArithmeticException on overflow. Division uses
// checkedDivide rather than Math.divideExact, which requires Java 18+ and so is unavailable to
// the Java 17 release the cores are compiled for.

private fun checkedDivide(a: Int, b: Int): Int =
    if (a == Int.MIN_VALUE && b == -1) throw ArithmeticException("integer overflow") else a / b

private fun checkedDivide(a: Long, b: Long): Long =
    if (a == Long.MIN_VALUE && b == -1L) throw ArithmeticException("long overflow") else a / b

private val checkedIntInstance: OverflowCheckedArithmetic<Int> = object : OverflowCheckedArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = Math.addExact(this, other)
    override fun Int.subtract(other: Int): Int = Math.subtractExact(this, other)
    override fun Int.multiply(other: Int): Int = Math.multiplyExact(this, other)
    override fun Int.divide(other: Int): Int = checkedDivide(this, other)
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)
}

private val checkedLongInstance: OverflowCheckedArithmetic<Long> = object : OverflowCheckedArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = Math.addExact(this, other)
    override fun Long.subtract(other: Long): Long = Math.subtractExact(this, other)
    override fun Long.multiply(other: Long): Long = Math.multiplyExact(this, other)
    override fun Long.divide(other: Long): Long = checkedDivide(this, other)
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
        override fun Int.divide(other: Int): Int = checkedDivide(this, other)
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
        override fun Long.divide(other: Long): Long = checkedDivide(this, other)
        override fun Long.rem(other: Long): Long = this % other
        override fun Long.compareTo(other: Long): Int = this.compareTo(other)

        override fun Long.negate(): Long = Math.negateExact(this)
        override fun Long.abs(): Long = Math.absExact(this)
    }

val OverflowCheckedSignedArithmetic.Companion.int: OverflowCheckedSignedArithmetic<Int>
    get() = checkedSignedIntInstance

val OverflowCheckedSignedArithmetic.Companion.long: OverflowCheckedSignedArithmetic<Long>
    get() = checkedSignedLongInstance
