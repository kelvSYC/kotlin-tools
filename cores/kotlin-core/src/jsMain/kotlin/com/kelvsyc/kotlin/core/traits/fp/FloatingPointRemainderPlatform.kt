package com.kelvsyc.kotlin.core.traits.fp

internal actual fun ieee754RemFloat(x: Float, y: Float): Float = ieee754RemFloatEmulated(x, y)
internal actual fun ieee754RemDouble(x: Double, y: Double): Double = ieee754RemDoubleEmulated(x, y)
