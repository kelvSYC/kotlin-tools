package com.kelvsyc.kotlin.guava

import com.google.common.primitives.Primitives
import com.google.common.reflect.TypeToken
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

/**
 * Returns a [TypeToken] for this type.
 *
 * Nullability is not preserved. Non-nullable primitive types (e.g. [Int]) produce a [TypeToken]
 * wrapping the Java primitive (e.g. `int.class`), not the boxed form. Use [typeTokenOf] if you
 * want primitives coerced to their boxed equivalents.
 */
@OptIn(ExperimentalStdlibApi::class)
fun KType.toTypeToken(): TypeToken<*> = TypeToken.of(javaType)

/**
 * Returns a [TypeToken] for the reified type [T].
 *
 * Non-nullable primitive types (e.g. [Int]) are coerced to their boxed equivalents
 * (e.g. [TypeToken]<[Integer]>), matching the behaviour of `object : TypeToken<T>() {}`.
 * Nullability is not preserved. Types with Kotlin declaration-site variance (e.g. [List]) produce
 * invariant [TypeToken]s, unlike the anonymous-class idiom which captures wildcards from the bytecode.
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> typeTokenOf(): TypeToken<T> {
    val javaType = typeOf<T>().javaType
    val type = if (javaType is Class<*> && javaType.isPrimitive) Primitives.wrap(javaType) else javaType
    @Suppress("UNCHECKED_CAST")
    return TypeToken.of(type) as TypeToken<T>
}
