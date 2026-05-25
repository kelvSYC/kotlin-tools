package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

expect val FloatingPointExp2.Companion.float: FloatingPointExp2<Float>
expect val FloatingPointExp2.Companion.double: FloatingPointExp2<Double>
expect val FloatingPointExp2.Companion.bfloat16: FloatingPointExp2<BFloat16>
expect val FloatingPointExp2.Companion.float16: FloatingPointExp2<Float16>
