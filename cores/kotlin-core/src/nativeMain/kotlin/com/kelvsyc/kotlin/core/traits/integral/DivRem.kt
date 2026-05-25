package com.kelvsyc.kotlin.core.traits.integral

import platform.posix.div as posixDiv
import platform.posix.lldiv

private val intInstance: DivRem<Int> = object : DivRem<Int> {
    override fun Int.divRem(other: Int): DivRemResult<Int> =
        posixDiv(this, other).useContents { DivRemResult(quot, rem) }
}

private val longInstance: DivRem<Long> = object : DivRem<Long> {
    override fun Long.divRem(other: Long): DivRemResult<Long> =
        lldiv(this, other).useContents { DivRemResult(quot, rem) }
}

val DivRem.Companion.int: DivRem<Int> get() = intInstance
val DivRem.Companion.long: DivRem<Long> get() = longInstance
