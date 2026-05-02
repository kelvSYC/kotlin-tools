package com.kelvsyc.kotlin.guava

import com.google.common.annotations.Beta
import com.google.common.escape.Escaper
import com.google.common.escape.Escapers

@Beta
fun buildEscaper(block: Escapers.Builder.() -> Unit): Escaper = Escapers.builder().apply(block).build()
