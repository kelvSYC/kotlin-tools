package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double

private val doubleInstance: TwoDiv<Double> by lazy {
    TwoDiv.from(FloatingPointArithmetic.double, FusedMultiplyAdd.double)
}

/**
 * [TwoDiv] instance for [Float].
 *
 * On Kotlin/JVM this is backed by [FloatingPointArithmetic.float][FloatingPointArithmetic.Companion.float]
 * and [FusedMultiplyAdd.float][com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd.Companion.float],
 * which use native `binary32` hardware arithmetic and hardware FMA respectively.
 * On Kotlin/JS, where [Float] arithmetic executes at `binary64` precision, this is backed by a strict
 * `binary32` arithmetic that round-trips each result through [Float.toRawBits] and
 * [Float.fromBits][Float.Companion.fromBits] to force correct rounding, paired with an emulated FMA.
 */
expect val TwoDiv.Companion.float: TwoDiv<Float>
val TwoDiv.Companion.double: TwoDiv<Double> get() = doubleInstance
