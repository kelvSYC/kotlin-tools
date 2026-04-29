package com.kelvsyc.kotlin.core

/**
 * Packs a biased exponent and a 23-bit significand into the lower 31 bits of a BID32 word (sign excluded).
 *
 * Uses the normal encoding path when `sig < 2^23`, and the large-significand path otherwise.
 * The sign bit must be OR-ed in by the caller.
 */
internal fun bidFloat32Pack(biasedExp: Int, sig: Int): Int =
    if (sig < 0x800000) {
        val combination = (biasedExp shl 3) or (sig ushr 20)
        val continuation = sig and 0xFFFFF
        (combination shl 20) or continuation
    } else {
        val combination = 0x600 or (biasedExp shl 1) or ((sig ushr 20) and 1)
        val low21 = sig and 0x1FFFFF
        (combination shl 20) or low21
    }
