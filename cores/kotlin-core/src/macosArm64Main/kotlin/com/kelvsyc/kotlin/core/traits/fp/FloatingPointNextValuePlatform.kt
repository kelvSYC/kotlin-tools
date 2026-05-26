package com.kelvsyc.kotlin.core.traits.fp

import platform.posix.nextafterf

internal actual fun nextUpFloat(x: Float): Float = nextafterf(x, Float.POSITIVE_INFINITY)
internal actual fun nextDownFloat(x: Float): Float = nextafterf(x, Float.NEGATIVE_INFINITY)
