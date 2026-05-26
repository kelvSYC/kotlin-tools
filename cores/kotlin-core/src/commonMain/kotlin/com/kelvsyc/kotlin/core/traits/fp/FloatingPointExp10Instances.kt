package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

expect val FloatingPointExp10.Companion.float: FloatingPointExp10<Float>
expect val FloatingPointExp10.Companion.double: FloatingPointExp10<Double>
expect val FloatingPointExp10.Companion.bfloat16: FloatingPointExp10<BFloat16>
expect val FloatingPointExp10.Companion.float16: FloatingPointExp10<Float16>
