package com.kelvsyc.kotlin.decimal

/** Rounding modes supported by Decimal.js, numbered 0–9 matching its own constants. */
enum class DecimalRounding(internal val code: Int) {
    UP(0),
    DOWN(1),
    CEIL(2),
    FLOOR(3),
    HALF_UP(4),
    HALF_DOWN(5),
    HALF_EVEN(6),
    HALF_CEIL(7),
    HALF_FLOOR(8),
    EUCLID(9),
}

/** Rounds to [d] decimal places using [rounding]. */
fun Decimal.toDecimalPlaces(d: Int, rounding: DecimalRounding): Decimal = toDecimalPlaces(d, rounding.code)

/** Rounds to [d] significant digits using [rounding]. */
fun Decimal.toSignificantDigits(d: Int, rounding: DecimalRounding): Decimal = toSignificantDigits(d, rounding.code)
