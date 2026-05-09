@file:JvmName("TwoSumInstancesJvm")

package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.float

private val floatInstance: TwoSum<Float> by lazy { TwoSum.from(FloatingPointArithmetic.float) }

actual val TwoSum.Companion.float: TwoSum<Float> get() = floatInstance
