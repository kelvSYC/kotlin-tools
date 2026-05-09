package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.strictFloatArithmetic

private val floatInstance: TwoSum<Float> by lazy { TwoSum.from(strictFloatArithmetic) }

actual val TwoSum.Companion.float: TwoSum<Float> get() = floatInstance
