package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

private val floatInstance: FloatingPointExp2<Float> = object : FloatingPointExp2<Float> {
    override fun Float.exp2(): Float = exp2FloatEmulated(this)
}

private val doubleInstance: FloatingPointExp2<Double> = object : FloatingPointExp2<Double> {
    override fun Double.exp2(): Double = exp2DoubleEmulated(this)
}

private val bfloat16Instance: FloatingPointExp2<BFloat16> = object : FloatingPointExp2<BFloat16> {
    override fun BFloat16.exp2(): BFloat16 = BFloat16(exp2FloatEmulated(toFloat()))
}

private val float16Instance: FloatingPointExp2<Float16> = object : FloatingPointExp2<Float16> {
    override fun Float16.exp2(): Float16 = Float16(exp2FloatEmulated(toFloat()))
}

actual val FloatingPointExp2.Companion.float: FloatingPointExp2<Float>
    get() = floatInstance

actual val FloatingPointExp2.Companion.double: FloatingPointExp2<Double>
    get() = doubleInstance

actual val FloatingPointExp2.Companion.bfloat16: FloatingPointExp2<BFloat16>
    get() = bfloat16Instance

actual val FloatingPointExp2.Companion.float16: FloatingPointExp2<Float16>
    get() = float16Instance
