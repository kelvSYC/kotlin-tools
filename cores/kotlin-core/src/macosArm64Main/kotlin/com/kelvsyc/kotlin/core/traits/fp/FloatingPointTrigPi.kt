package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import macmath.kotlin_asinpi
import macmath.kotlin_asinpif
import macmath.kotlin_acospi
import macmath.kotlin_acospif
import macmath.kotlin_atanpi
import macmath.kotlin_atanpif
import macmath.kotlin_atan2pi
import macmath.kotlin_atan2pif
import macmath.kotlin_cospi
import macmath.kotlin_cospif
import macmath.kotlin_sinpi
import macmath.kotlin_sinpif
import macmath.kotlin_tanpi
import macmath.kotlin_tanpif

private val doubleInstance: FloatingPointTrigPi<Double> = object : FloatingPointTrigPi<Double> {
    override fun Double.sinPi(): Double = kotlin_sinpi(this)
    override fun Double.cosPi(): Double = kotlin_cospi(this)
    override fun Double.tanPi(): Double = kotlin_tanpi(this)
    override fun Double.asinPi(): Double = kotlin_asinpi(this)
    override fun Double.acosPi(): Double = kotlin_acospi(this)
    override fun Double.atanPi(): Double = kotlin_atanpi(this)
    override fun Double.atan2Pi(x: Double): Double = kotlin_atan2pi(this, x)
}

private val floatInstance: FloatingPointTrigPi<Float> = object : FloatingPointTrigPi<Float> {
    override fun Float.sinPi(): Float = kotlin_sinpif(this)
    override fun Float.cosPi(): Float = kotlin_cospif(this)
    override fun Float.tanPi(): Float = kotlin_tanpif(this)
    override fun Float.asinPi(): Float = kotlin_asinpif(this)
    override fun Float.acosPi(): Float = kotlin_acospif(this)
    override fun Float.atanPi(): Float = kotlin_atanpif(this)
    override fun Float.atan2Pi(x: Float): Float = kotlin_atan2pif(this, x)
}

private val bfloat16Instance: FloatingPointTrigPi<BFloat16> = object : FloatingPointTrigPi<BFloat16> {
    override fun BFloat16.sinPi(): BFloat16 = BFloat16(kotlin_sinpif(toFloat()))
    override fun BFloat16.cosPi(): BFloat16 = BFloat16(kotlin_cospif(toFloat()))
    override fun BFloat16.tanPi(): BFloat16 = BFloat16(kotlin_tanpif(toFloat()))
    override fun BFloat16.asinPi(): BFloat16 = BFloat16(kotlin_asinpif(toFloat()))
    override fun BFloat16.acosPi(): BFloat16 = BFloat16(kotlin_acospif(toFloat()))
    override fun BFloat16.atanPi(): BFloat16 = BFloat16(kotlin_atanpif(toFloat()))
    override fun BFloat16.atan2Pi(x: BFloat16): BFloat16 = BFloat16(kotlin_atan2pif(toFloat(), x.toFloat()))
}

private val float16Instance: FloatingPointTrigPi<Float16> = object : FloatingPointTrigPi<Float16> {
    override fun Float16.sinPi(): Float16 = Float16(kotlin_sinpif(toFloat()))
    override fun Float16.cosPi(): Float16 = Float16(kotlin_cospif(toFloat()))
    override fun Float16.tanPi(): Float16 = Float16(kotlin_tanpif(toFloat()))
    override fun Float16.asinPi(): Float16 = Float16(kotlin_asinpif(toFloat()))
    override fun Float16.acosPi(): Float16 = Float16(kotlin_acospif(toFloat()))
    override fun Float16.atanPi(): Float16 = Float16(kotlin_atanpif(toFloat()))
    override fun Float16.atan2Pi(x: Float16): Float16 = Float16(kotlin_atan2pif(toFloat(), x.toFloat()))
}

actual val FloatingPointTrigPi.Companion.double: FloatingPointTrigPi<Double> get() = doubleInstance
actual val FloatingPointTrigPi.Companion.float: FloatingPointTrigPi<Float> get() = floatInstance
actual val FloatingPointTrigPi.Companion.bfloat16: FloatingPointTrigPi<BFloat16> get() = bfloat16Instance
actual val FloatingPointTrigPi.Companion.float16: FloatingPointTrigPi<Float16> get() = float16Instance
