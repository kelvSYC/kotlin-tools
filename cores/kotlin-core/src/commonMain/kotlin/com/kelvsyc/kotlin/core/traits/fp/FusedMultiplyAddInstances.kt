package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

expect val FusedMultiplyAdd.Companion.float: FusedMultiplyAdd<Float>
expect val FusedMultiplyAdd.Companion.double: FusedMultiplyAdd<Double>
expect val FusedMultiplyAdd.Companion.bfloat16: FusedMultiplyAdd<BFloat16>
expect val FusedMultiplyAdd.Companion.float16: FusedMultiplyAdd<Float16>
