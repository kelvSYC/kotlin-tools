package com.kelvsyc.kotlin.commons.lang.reflect

import org.apache.commons.lang3.reflect.TypeLiteral

/**
 * Returns a [TypeLiteral] capturing the reified type [T].
 *
 * This is a convenience wrapper for the anonymous subclass idiom required to capture generic type
 * information at runtime:
 * ```kotlin
 * val literal = typeLiteralOf<List<String>>()
 * ```
 */
inline fun <reified T : Any> typeLiteralOf(): TypeLiteral<T> = object : TypeLiteral<T>() {}
