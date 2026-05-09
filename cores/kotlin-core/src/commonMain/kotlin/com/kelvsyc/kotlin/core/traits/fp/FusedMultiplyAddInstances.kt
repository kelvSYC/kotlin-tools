package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * [FusedMultiplyAdd] instance for [Float].
 *
 * On Kotlin/JVM this delegates to [java.lang.Math.fma], which uses hardware FMA when available.
 * On Kotlin/JS this uses a software emulation (Boldo-Melquiond algorithm via Veltkamp-Dekker
 * [TwoProduct][com.kelvsyc.kotlin.core.traits.dd.TwoProduct]) backed by strict `binary32`
 * arithmetic that round-trips each intermediate result through [Float.toRawBits] and
 * [Float.fromBits][Float.Companion.fromBits] to force correct rounding.
 */
expect val FusedMultiplyAdd.Companion.float: FusedMultiplyAdd<Float>
expect val FusedMultiplyAdd.Companion.double: FusedMultiplyAdd<Double>
expect val FusedMultiplyAdd.Companion.bfloat16: FusedMultiplyAdd<BFloat16>
expect val FusedMultiplyAdd.Companion.float16: FusedMultiplyAdd<Float16>
