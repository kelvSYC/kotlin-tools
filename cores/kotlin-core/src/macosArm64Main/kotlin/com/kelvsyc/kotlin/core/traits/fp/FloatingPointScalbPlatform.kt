package com.kelvsyc.kotlin.core.traits.fp

import platform.posix.scalbn
import platform.posix.scalbnf

internal actual fun scalbFloat(x: Float, n: Int): Float = scalbnf(x, n)
internal actual fun scalbDouble(x: Double, n: Int): Double = scalbn(x, n)
