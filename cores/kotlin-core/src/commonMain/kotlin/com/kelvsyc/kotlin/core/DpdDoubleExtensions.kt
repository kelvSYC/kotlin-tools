package com.kelvsyc.kotlin.core

/** Copies this array's elements into a new [DpdDoubleArray], interpreting each [Long] as a [DpdDouble] bit pattern. */
fun LongArray.toDpdDoubleArray(): DpdDoubleArray = DpdDoubleArray(this)
