package com.kelvsyc.kotlin.core

/** Converts this value to a [BFloat16] by narrowing using round-to-nearest-even. */
fun Float.toBFloat16(): BFloat16 = BFloat16(BFloat16.converter(this))

/** Converts this value to a [BFloat16] by narrowing through [Float] using round-to-nearest-even. */
fun Double.toBFloat16(): BFloat16 = toFloat().toBFloat16()

/** Copies this array's elements into a new [BFloat16Array], interpreting each [Short] as a [BFloat16] bit pattern. */
fun ShortArray.toBFloat16Array(): BFloat16Array = BFloat16Array(this)
