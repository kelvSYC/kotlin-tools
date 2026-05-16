package com.kelvsyc.kotlin.core.traits.integral

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
