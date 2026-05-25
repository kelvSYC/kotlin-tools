package com.kelvsyc.kotlin.core.traits.fp

import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import mathext.kotlin_sincos as nativeSincos
import mathext.kotlin_sincosf as nativeSincosf

private val floatInstance: FloatingPointSinCos<Float> = object : FloatingPointSinCos<Float> {
    override fun Float.sincos(): SinCosResult<Float> = memScoped {
        val s = alloc<FloatVar>()
        val c = alloc<FloatVar>()
        nativeSincosf(this@sincos, s.ptr, c.ptr)
        SinCosResult(s.value, c.value)
    }
}

private val doubleInstance: FloatingPointSinCos<Double> = object : FloatingPointSinCos<Double> {
    override fun Double.sincos(): SinCosResult<Double> = memScoped {
        val s = alloc<DoubleVar>()
        val c = alloc<DoubleVar>()
        nativeSincos(this@sincos, s.ptr, c.ptr)
        SinCosResult(s.value, c.value)
    }
}

val FloatingPointSinCos.Companion.float: FloatingPointSinCos<Float> get() = floatInstance
val FloatingPointSinCos.Companion.double: FloatingPointSinCos<Double> get() = doubleInstance
