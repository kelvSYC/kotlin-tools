package com.kelvsyc.kotlin.decimal

import com.kelvsyc.kotlin.core.Converter

// ── Double ↔ Decimal ──────────────────────────────────────────────────────────
//
// Lossy: Double has ~15–17 significant digits; Decimal is arbitrary precision.
// Round-tripping a Decimal through Double discards digits beyond Double's range.

fun Decimal.toKotlinDouble(): Double = toNumber()

fun Double.toDecimal(): Decimal = Decimal(this)

val decimalToDouble: Converter<Decimal, Double> = Converter.of(
    forward = { it.toKotlinDouble() },
    backward = { it.toDecimal() },
)

// ── String ↔ Decimal ──────────────────────────────────────────────────────────
//
// Lossless: Decimal.js parses and formats strings without rounding.

fun Decimal.toKotlinString(): String = toString()

fun String.toDecimal(): Decimal = Decimal(this)

val decimalToString: Converter<Decimal, String> = Converter.of(
    forward = { it.toKotlinString() },
    backward = { it.toDecimal() },
)

// ── Long ↔ Decimal ────────────────────────────────────────────────────────────
//
// Lossless for values in the integer range. toKotlinLong() is only safe when isInteger()
// holds and the value fits in a Long. Conversion via String avoids Kotlin Long boxing issues.

fun Decimal.toKotlinLong(): Long = toNumber().toLong()

fun Long.toDecimal(): Decimal = Decimal(toString())

val decimalToLong: Converter<Decimal, Long> = Converter.of(
    forward = { it.toKotlinLong() },
    backward = { it.toDecimal() },
)
