package com.kelvsyc.kotlin.core

/** Copies this array's elements into a new [DpdFloatArray], interpreting each [Int] as a [DpdFloat] bit pattern. */
fun IntArray.toDpdFloatArray(): DpdFloatArray = DpdFloatArray(this)
