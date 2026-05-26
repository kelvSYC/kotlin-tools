package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

private val floatInstance: FloatingPointExp10<Float> = object : FloatingPointExp10<Float> {
    override fun Float.exp10(): Float = exp10FloatEmulated(this)
}

private val doubleInstance: FloatingPointExp10<Double> = object : FloatingPointExp10<Double> {
    override fun Double.exp10(): Double = exp10DoubleEmulated(this)
}

private val bfloat16Instance: FloatingPointExp10<BFloat16> = object : FloatingPointExp10<BFloat16> {
    override fun BFloat16.exp10(): BFloat16 = BFloat16(exp10FloatEmulated(toFloat()))
}

private val float16Instance: FloatingPointExp10<Float16> = object : FloatingPointExp10<Float16> {
    override fun Float16.exp10(): Float16 = Float16(exp10FloatEmulated(toFloat()))
}

actual val FloatingPointExp10.Companion.float: FloatingPointExp10<Float> get() = floatInstance
actual val FloatingPointExp10.Companion.double: FloatingPointExp10<Double> get() = doubleInstance
actual val FloatingPointExp10.Companion.bfloat16: FloatingPointExp10<BFloat16> get() = bfloat16Instance
actual val FloatingPointExp10.Companion.float16: FloatingPointExp10<Float16> get() = float16Instance
