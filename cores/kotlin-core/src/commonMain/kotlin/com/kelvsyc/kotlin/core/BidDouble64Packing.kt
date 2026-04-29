package com.kelvsyc.kotlin.core

/**
 * Packs a biased exponent and a 53-bit significand into the lower 63 bits of a BID64 word (sign excluded).
 *
 * Uses the normal encoding path when `sig < 2^53`, and the large-significand path otherwise.
 * The sign bit must be OR-ed in by the caller.
 */
internal fun bidDouble64Pack(biasedExp: Int, sig: Long): Long =
    if (sig < 0x20_0000_0000_0000L) {
        val combination = (biasedExp.toLong() shl 3) or (sig ushr 50)
        val continuation = sig and 0x3_FFFF_FFFF_FFFFL
        (combination shl 50) or continuation
    } else {
        val combination = 0x1800L or (biasedExp.toLong() shl 1) or ((sig ushr 50) and 1L)
        val low51 = sig and 0x7_FFFF_FFFF_FFFFL
        (combination shl 50) or low51
    }
