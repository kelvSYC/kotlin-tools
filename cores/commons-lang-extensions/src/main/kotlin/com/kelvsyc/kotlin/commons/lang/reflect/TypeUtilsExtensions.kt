package com.kelvsyc.kotlin.commons.lang.reflect

import org.apache.commons.lang3.reflect.TypeUtils
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

/**
 * Builds a [WildcardType] by applying a configuration action to a [TypeUtils.WildcardTypeBuilder].
 *
 * Example:
 * ```kotlin
 * val wildcard = buildWildcardType {
 *     withUpperBounds(Number::class.java)
 * }
 * ```
 */
fun buildWildcardType(action: TypeUtils.WildcardTypeBuilder.() -> Unit): WildcardType =
    TypeUtils.wildcardType().apply(action).build()

/**
 * Returns a [ParameterizedType] for [raw] with the given [typeArguments].
 *
 * Example:
 * ```kotlin
 * val type = parameterizedTypeOf(List::class, String::class.java)  // List<String>
 * ```
 */
fun parameterizedTypeOf(raw: KClass<*>, vararg typeArguments: Type): ParameterizedType =
    TypeUtils.parameterize(raw.java, *typeArguments)

/**
 * Returns a [ParameterizedType] for the reified type [T] with the given [typeArguments].
 *
 * Example:
 * ```kotlin
 * val type = parameterizedTypeOf<List<*>>(String::class.java)  // List<String>
 * ```
 */
inline fun <reified T : Any> parameterizedTypeOf(vararg typeArguments: Type): ParameterizedType =
    TypeUtils.parameterize(T::class.java, *typeArguments)
