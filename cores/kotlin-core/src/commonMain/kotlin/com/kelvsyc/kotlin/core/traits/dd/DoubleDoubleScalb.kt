package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointScalb
import com.kelvsyc.kotlin.core.traits.fp.scalbDouble

private val doubleDoubleInstance: FloatingPointScalb<DoubleDouble> =
    object : FloatingPointScalb<DoubleDouble> {
        override fun DoubleDouble.scalb(n: Int): DoubleDouble =
            DoubleDouble(scalbDouble(high, n), scalbDouble(low, n))
    }

/**
 * [FloatingPointScalb] instance for [DoubleDouble].
 *
 * Applies [scalbDouble] independently to the high and low components, which is equivalent to
 * multiplying the extended-precision value by `2^n`. This matches the algorithm used by Apache
 * Commons Numbers [DD][org.apache.commons.numbers.core.DD].
 */
val FloatingPointScalb.Companion.doubleDouble: FloatingPointScalb<DoubleDouble>
    get() = doubleDoubleInstance
