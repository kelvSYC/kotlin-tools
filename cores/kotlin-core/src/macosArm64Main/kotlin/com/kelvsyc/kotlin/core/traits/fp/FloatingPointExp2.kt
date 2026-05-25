package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import platform.posix.exp2 as posixExp2
import platform.posix.exp2f as posixExp2f

private val floatInstance: FloatingPointExp2<Float> = object : FloatingPointExp2<Float> {
    override fun Float.exp2(): Float = posixExp2f(this)
}

private val doubleInstance: FloatingPointExp2<Double> = object : FloatingPointExp2<Double> {
    override fun Double.exp2(): Double = posixExp2(this)
}

private val bfloat16Instance: FloatingPointExp2<BFloat16> = object : FloatingPointExp2<BFloat16> {
    override fun BFloat16.exp2(): BFloat16 = BFloat16(posixExp2f(toFloat()))
}

private val float16Instance: FloatingPointExp2<Float16> = object : FloatingPointExp2<Float16> {
    override fun Float16.exp2(): Float16 = Float16(posixExp2f(toFloat()))
}

actual val FloatingPointExp2.Companion.float: FloatingPointExp2<Float>
    get() = floatInstance

actual val FloatingPointExp2.Companion.double: FloatingPointExp2<Double>
    get() = doubleInstance

actual val FloatingPointExp2.Companion.bfloat16: FloatingPointExp2<BFloat16>
    get() = bfloat16Instance

actual val FloatingPointExp2.Companion.float16: FloatingPointExp2<Float16>
    get() = float16Instance
