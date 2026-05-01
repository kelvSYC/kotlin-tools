package com.kelvsyc.kotlin.core

/** Copies this array's elements into a new [BidFloatArray], interpreting each [Int] as a [BidFloat] bit pattern. */
fun IntArray.toBidFloatArray(): BidFloatArray = BidFloatArray(this)
