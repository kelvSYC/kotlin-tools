package com.kelvsyc.kotlin.core.traits

internal actual fun ieee754RemFloat(x: Float, y: Float): Float =
    Math.IEEEremainder(x.toDouble(), y.toDouble()).toFloat()

internal actual fun ieee754RemDouble(x: Double, y: Double): Double =
    Math.IEEEremainder(x, y)
