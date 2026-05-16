package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.div
import com.kelvsyc.kotlin.core.longConverter
import com.kelvsyc.kotlin.core.minus
import com.kelvsyc.kotlin.core.plus
import com.kelvsyc.kotlin.core.times

// Double-promotion is exact for Int: JavaScript's number has a 53-bit mantissa, so all 32-bit
// integer values are representable without rounding.
private fun Double.toCheckedInt(): Int {
    if (this > Int.MAX_VALUE || this < Int.MIN_VALUE) throw ArithmeticException("integer overflow")
    return toInt()
}

private val checkedIntInstance: OverflowCheckedArithmetic<Int> = object : OverflowCheckedArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = (toDouble() + other.toDouble()).toCheckedInt()
    override fun Int.subtract(other: Int): Int = (toDouble() - other.toDouble()).toCheckedInt()
    override fun Int.multiply(other: Int): Int = (toDouble() * other.toDouble()).toCheckedInt()
    override fun Int.divide(other: Int): Int {
        if (other == 0) throw ArithmeticException("/ by zero")
        if (this == Int.MIN_VALUE && other == -1) throw ArithmeticException("integer overflow")
        return this / other
    }
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)
}

private val checkedSignedIntInstance: OverflowCheckedSignedArithmetic<Int> = object : OverflowCheckedSignedArithmetic<Int> {
    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun Int.add(other: Int): Int = (toDouble() + other.toDouble()).toCheckedInt()
    override fun Int.subtract(other: Int): Int = (toDouble() - other.toDouble()).toCheckedInt()
    override fun Int.multiply(other: Int): Int = (toDouble() * other.toDouble()).toCheckedInt()
    override fun Int.divide(other: Int): Int {
        if (other == 0) throw ArithmeticException("/ by zero")
        if (this == Int.MIN_VALUE && other == -1) throw ArithmeticException("integer overflow")
        return this / other
    }
    override fun Int.rem(other: Int): Int = this % other
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)

    override fun Int.negate(): Int {
        if (this == Int.MIN_VALUE) throw ArithmeticException("integer overflow")
        return -this
    }
    override fun Int.abs(): Int {
        if (this == Int.MIN_VALUE) throw ArithmeticException("integer overflow")
        return kotlin.math.abs(this)
    }
}

val OverflowCheckedArithmetic.Companion.int: OverflowCheckedArithmetic<Int>
    get() = checkedIntInstance

val OverflowCheckedSignedArithmetic.Companion.int: OverflowCheckedSignedArithmetic<Int>
    get() = checkedSignedIntInstance

// ── Long: BigInt-promotion ────────────────────────────────────────────────────
//
// Long cannot use Double-promotion: 64-bit values exceed the 53-bit Double mantissa.
// Instead, both operands are widened to BigInt (arbitrary precision), the operation is performed,
// and the result is range-checked before narrowing back to Long.

@Suppress("NOTHING_TO_INLINE")
private inline fun Long.toBigInt(): BigInt = bigIntOf(this.toString())

private fun BigInt.toLongChecked(): Long = BigInt.longConverter(this)

// Capture Long rem and compareTo at file scope to avoid member-extension shadowing inside the
// anonymous objects below (same pattern as _intFloorDiv/_intMod in SignedIntegerArithmetic.kt).
private val _longRemCapture: (Long, Long) -> Long = { a, b -> a % b }
private val _longCompareCapture: (Long, Long) -> Int = { a, b -> a.compareTo(b) }

private val checkedLongInstance: OverflowCheckedArithmetic<Long> = object : OverflowCheckedArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = (toBigInt() + other.toBigInt()).toLongChecked()
    override fun Long.subtract(other: Long): Long = (toBigInt() - other.toBigInt()).toLongChecked()
    override fun Long.multiply(other: Long): Long = (toBigInt() * other.toBigInt()).toLongChecked()
    override fun Long.divide(other: Long): Long {
        if (other == 0L) throw ArithmeticException("/ by zero")
        return (toBigInt() / other.toBigInt()).toLongChecked()
    }
    override fun Long.rem(other: Long): Long {
        if (other == 0L) throw ArithmeticException("/ by zero")
        return _longRemCapture(this, other)  // remainder can't overflow; bridge avoids recursion
    }
    override fun Long.compareTo(other: Long): Int = _longCompareCapture(this, other)
}

private val checkedSignedLongInstance: OverflowCheckedSignedArithmetic<Long> = object : OverflowCheckedSignedArithmetic<Long> {
    override val zero: Long get() = 0L
    override val one: Long get() = 1L

    override fun Long.add(other: Long): Long = (toBigInt() + other.toBigInt()).toLongChecked()
    override fun Long.subtract(other: Long): Long = (toBigInt() - other.toBigInt()).toLongChecked()
    override fun Long.multiply(other: Long): Long = (toBigInt() * other.toBigInt()).toLongChecked()
    override fun Long.divide(other: Long): Long {
        if (other == 0L) throw ArithmeticException("/ by zero")
        return (toBigInt() / other.toBigInt()).toLongChecked()
    }
    override fun Long.rem(other: Long): Long {
        if (other == 0L) throw ArithmeticException("/ by zero")
        return _longRemCapture(this, other)
    }
    override fun Long.compareTo(other: Long): Int = _longCompareCapture(this, other)

    override fun Long.negate(): Long {
        if (this == Long.MIN_VALUE) throw ArithmeticException("integer overflow")
        return -this
    }
    override fun Long.abs(): Long {
        if (this == Long.MIN_VALUE) throw ArithmeticException("integer overflow")
        return if (this < 0L) -this else this
    }
}

val OverflowCheckedArithmetic.Companion.long: OverflowCheckedArithmetic<Long>
    get() = checkedLongInstance

val OverflowCheckedSignedArithmetic.Companion.long: OverflowCheckedSignedArithmetic<Long>
    get() = checkedSignedLongInstance
