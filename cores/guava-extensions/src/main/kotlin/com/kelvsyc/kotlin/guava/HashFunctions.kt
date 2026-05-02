package com.kelvsyc.kotlin.guava

import com.google.common.hash.HashCode
import com.google.common.hash.HashFunction
import com.google.common.hash.Hasher

fun HashFunction.hash(block: Hasher.() -> Unit): HashCode = newHasher().apply(block).hash()

fun HashFunction.hash(expectedInputSize: Int, block: Hasher.() -> Unit): HashCode =
    newHasher(expectedInputSize).apply(block).hash()
