package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.BigInt
import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.div
import com.kelvsyc.kotlin.core.minus
import com.kelvsyc.kotlin.core.plus
import com.kelvsyc.kotlin.core.times
import com.kelvsyc.kotlin.core.unaryMinus

// Private inline bridges for operations where the interface member extension name matches the
// Kotlin operator name (preventing use of the public operator inside the anonymous object).
// `rem` clashes with `IntegerArithmetic.rem`; `compareTo` clashes with `IntegerArithmetic.compareTo`.
// `abs` and `arithmeticRightShift` have no operator equivalent but still need js() via inline.

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInt.jsRem(other: BigInt): BigInt {
    val self = this; return js("self % other")
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInt.jsCompareTo(other: BigInt): Int {
    val self = this; return js("self < other ? -1 : self > other ? 1 : 0")
}

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInt.jsNeg(): BigInt {
    val self = this; return js("-self")
}

// abs: negate if negative (avoids BigInt literal `0n` which Kotlin/JS IR doesn't accept in js())
@Suppress("NOTHING_TO_INLINE")
private inline fun BigInt.jsAbs(): BigInt = if (jsCompareTo(bigIntOf(0)) < 0) jsNeg() else this

@Suppress("NOTHING_TO_INLINE")
private inline fun BigInt.jsShr(bits: Int): BigInt {
    val self = this; return js("self >> BigInt(bits)")
}

// A single object satisfies both traits; two companion properties expose it under each interface.
private val bigIntInstance = object : SignedIntegerArithmetic<BigInt>, ArithmeticRightShift<BigInt> {
    override val zero: BigInt get() = bigIntOf(0)
    override val one: BigInt get() = bigIntOf(1)

    // `this + other` uses the imported inline `plus` (no naming conflict with interface's `add`)
    override fun BigInt.add(other: BigInt): BigInt = this + other
    override fun BigInt.subtract(other: BigInt): BigInt = this - other
    override fun BigInt.multiply(other: BigInt): BigInt = this * other

    override fun BigInt.divide(other: BigInt): BigInt {
        if (other.jsCompareTo(zero) == 0) throw ArithmeticException("/ by zero")
        return this / other  // imported `div` operator; interface uses `divide`, no naming conflict
    }

    override fun BigInt.rem(other: BigInt): BigInt {
        if (other.jsCompareTo(zero) == 0) throw ArithmeticException("/ by zero")
        return jsRem(other)  // must use bridge: Kotlin `%` resolves to `rem`, which is this override
    }

    override fun BigInt.compareTo(other: BigInt): Int = jsCompareTo(other)

    override fun BigInt.negate(): BigInt = -this  // imported `unaryMinus`; no conflict with `negate`

    override fun BigInt.abs(): BigInt = jsAbs()

    override fun BigInt.arithmeticRightShift(bits: Int): BigInt = jsShr(bits)
}

val SignedIntegerArithmetic.Companion.bigInt: SignedIntegerArithmetic<BigInt>
    get() = bigIntInstance

val ArithmeticRightShift.Companion.bigInt: ArithmeticRightShift<BigInt>
    get() = bigIntInstance
