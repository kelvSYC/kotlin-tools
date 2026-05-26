package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

expect val FloatingPointTrigPi.Companion.double: FloatingPointTrigPi<Double>
expect val FloatingPointTrigPi.Companion.float: FloatingPointTrigPi<Float>
expect val FloatingPointTrigPi.Companion.bfloat16: FloatingPointTrigPi<BFloat16>
expect val FloatingPointTrigPi.Companion.float16: FloatingPointTrigPi<Float16>
