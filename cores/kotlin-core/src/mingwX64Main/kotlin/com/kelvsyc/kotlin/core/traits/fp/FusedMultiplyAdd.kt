package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import platform.posix.fma as posixFma
import platform.posix.fmaf as posixFmaf

private val floatInstance: FusedMultiplyAdd<Float> = object : FusedMultiplyAdd<Float> {
    override fun fma(a: Float, b: Float, c: Float): Float = posixFmaf(a, b, c)
}

private val doubleInstance: FusedMultiplyAdd<Double> = object : FusedMultiplyAdd<Double> {
    override fun fma(a: Double, b: Double, c: Double): Double = posixFma(a, b, c)
}

private val bfloat16Instance: FusedMultiplyAdd<BFloat16> = object : FusedMultiplyAdd<BFloat16> {
    override fun fma(a: BFloat16, b: BFloat16, c: BFloat16): BFloat16 =
        BFloat16(posixFmaf(a.toFloat(), b.toFloat(), c.toFloat()))
}

private val float16Instance: FusedMultiplyAdd<Float16> = object : FusedMultiplyAdd<Float16> {
    override fun fma(a: Float16, b: Float16, c: Float16): Float16 =
        Float16(posixFmaf(a.toFloat(), b.toFloat(), c.toFloat()))
}

actual val FusedMultiplyAdd.Companion.float: FusedMultiplyAdd<Float>
    get() = floatInstance

actual val FusedMultiplyAdd.Companion.double: FusedMultiplyAdd<Double>
    get() = doubleInstance

actual val FusedMultiplyAdd.Companion.bfloat16: FusedMultiplyAdd<BFloat16>
    get() = bfloat16Instance

actual val FusedMultiplyAdd.Companion.float16: FusedMultiplyAdd<Float16>
    get() = float16Instance
