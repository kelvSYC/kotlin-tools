package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoSum
import com.kelvsyc.kotlin.core.traits.dd.from

private val floatInstance: TwoSum<Float> by lazy { TwoSum.from(FloatingPointArithmetic.float) }
private val doubleInstance: TwoSum<Double> by lazy { TwoSum.from(FloatingPointArithmetic.double) }

val TwoSum.Companion.float: TwoSum<Float> get() = floatInstance
val TwoSum.Companion.double: TwoSum<Double> get() = doubleInstance
