package com.kelvsyc.kotlin.core

/** Converts this value to a [Float16] by narrowing, using round-to-nearest-even. */
fun Float.toFloat16(): Float16 = Float16(Float16.converter(this))

/** Converts this value to a [Float16] by narrowing through [Float], using round-to-nearest-even. */
fun Double.toFloat16(): Float16 = toFloat().toFloat16()
