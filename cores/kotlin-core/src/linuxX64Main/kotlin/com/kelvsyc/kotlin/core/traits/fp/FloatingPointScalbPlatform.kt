package com.kelvsyc.kotlin.core.traits.fp

internal actual fun scalbFloat(x: Float, n: Int): Float = scalbFloatEmulated(x, n)
internal actual fun scalbDouble(x: Double, n: Int): Double = scalbDoubleEmulated(x, n)
