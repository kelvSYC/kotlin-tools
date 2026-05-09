package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.Binary64
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double

private val doubleInstance: TwoProduct<Double> by lazy {
    TwoProduct.from(FloatingPointArithmetic.double, Binary64.Companion)
}

/**
 * [TwoProduct] instance for [Float].
 *
 * On Kotlin/JVM this is backed by [FloatingPointArithmetic.float][FloatingPointArithmetic.Companion.float],
 * which uses native `binary32` hardware arithmetic.
 * On Kotlin/JS, where [Float] arithmetic executes at `binary64` precision, this is backed by a strict
 * `binary32` arithmetic that round-trips each result through [Float.toRawBits] and
 * [Float.fromBits][Float.Companion.fromBits] to force correct rounding.
 */
expect val TwoProduct.Companion.float: TwoProduct<Float>
val TwoProduct.Companion.double: TwoProduct<Double> get() = doubleInstance
