package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.double
import com.kelvsyc.kotlin.core.traits.float

private val floatInstance: TwoSum<Float> by lazy { TwoSum.from(FloatingPointArithmetic.float) }
private val doubleInstance: TwoSum<Double> by lazy { TwoSum.from(FloatingPointArithmetic.double) }

val TwoSum.Companion.float: TwoSum<Float> get() = floatInstance
val TwoSum.Companion.double: TwoSum<Double> get() = doubleInstance
