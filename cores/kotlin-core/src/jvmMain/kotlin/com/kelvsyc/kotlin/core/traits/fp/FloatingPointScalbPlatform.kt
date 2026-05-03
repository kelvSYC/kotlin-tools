package com.kelvsyc.kotlin.core.traits.fp

internal actual fun scalbFloat(x: Float, n: Int): Float = Math.scalb(x, n)
internal actual fun scalbDouble(x: Double, n: Int): Double = Math.scalb(x, n)
