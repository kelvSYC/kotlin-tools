package com.kelvsyc.kotlin.core.traits.fp

import platform.posix.remainder
import platform.posix.remainderf

internal actual fun ieee754RemFloat(x: Float, y: Float): Float = remainderf(x, y)
internal actual fun ieee754RemDouble(x: Double, y: Double): Double = remainder(x, y)
