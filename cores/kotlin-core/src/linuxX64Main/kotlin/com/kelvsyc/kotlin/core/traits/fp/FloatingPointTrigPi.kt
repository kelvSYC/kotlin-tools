package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

private val doubleInstance: FloatingPointTrigPi<Double> = object : FloatingPointTrigPi<Double> {
    override fun Double.sinPi(): Double = doubleSinPi(this)
    override fun Double.cosPi(): Double = doubleCosPi(this)
    override fun Double.tanPi(): Double = doubleTanPi(this)
    override fun Double.asinPi(): Double = doubleAsinPi(this)
    override fun Double.acosPi(): Double = doubleAcosPi(this)
    override fun Double.atanPi(): Double = doubleAtanPi(this)
    override fun Double.atan2Pi(x: Double): Double = doubleAtan2Pi(this, x)
}

private val floatInstance: FloatingPointTrigPi<Float> = object : FloatingPointTrigPi<Float> {
    override fun Float.sinPi(): Float = floatSinPi(this)
    override fun Float.cosPi(): Float = floatCosPi(this)
    override fun Float.tanPi(): Float = floatTanPi(this)
    override fun Float.asinPi(): Float = floatAsinPi(this)
    override fun Float.acosPi(): Float = floatAcosPi(this)
    override fun Float.atanPi(): Float = floatAtanPi(this)
    override fun Float.atan2Pi(x: Float): Float = floatAtan2Pi(this, x)
}

private val bfloat16Instance: FloatingPointTrigPi<BFloat16> = object : FloatingPointTrigPi<BFloat16> {
    override fun BFloat16.sinPi(): BFloat16 = BFloat16(floatSinPi(toFloat()))
    override fun BFloat16.cosPi(): BFloat16 = BFloat16(floatCosPi(toFloat()))
    override fun BFloat16.tanPi(): BFloat16 = BFloat16(floatTanPi(toFloat()))
    override fun BFloat16.asinPi(): BFloat16 = BFloat16(floatAsinPi(toFloat()))
    override fun BFloat16.acosPi(): BFloat16 = BFloat16(floatAcosPi(toFloat()))
    override fun BFloat16.atanPi(): BFloat16 = BFloat16(floatAtanPi(toFloat()))
    override fun BFloat16.atan2Pi(x: BFloat16): BFloat16 = BFloat16(floatAtan2Pi(toFloat(), x.toFloat()))
}

private val float16Instance: FloatingPointTrigPi<Float16> = object : FloatingPointTrigPi<Float16> {
    override fun Float16.sinPi(): Float16 = Float16(floatSinPi(toFloat()))
    override fun Float16.cosPi(): Float16 = Float16(floatCosPi(toFloat()))
    override fun Float16.tanPi(): Float16 = Float16(floatTanPi(toFloat()))
    override fun Float16.asinPi(): Float16 = Float16(floatAsinPi(toFloat()))
    override fun Float16.acosPi(): Float16 = Float16(floatAcosPi(toFloat()))
    override fun Float16.atanPi(): Float16 = Float16(floatAtanPi(toFloat()))
    override fun Float16.atan2Pi(x: Float16): Float16 = Float16(floatAtan2Pi(toFloat(), x.toFloat()))
}

actual val FloatingPointTrigPi.Companion.double: FloatingPointTrigPi<Double> get() = doubleInstance
actual val FloatingPointTrigPi.Companion.float: FloatingPointTrigPi<Float> get() = floatInstance
actual val FloatingPointTrigPi.Companion.bfloat16: FloatingPointTrigPi<BFloat16> get() = bfloat16Instance
actual val FloatingPointTrigPi.Companion.float16: FloatingPointTrigPi<Float16> get() = float16Instance
