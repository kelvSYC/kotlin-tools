package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.ClassToInstanceMap
import com.google.common.collect.ImmutableClassToInstanceMap
import com.google.common.collect.MutableClassToInstanceMap
import com.google.common.reflect.ImmutableTypeToInstanceMap
import com.google.common.reflect.MutableTypeToInstanceMap
import com.google.common.reflect.TypeToInstanceMap
import com.kelvsyc.kotlin.guava.typeTokenOf

/**
 * Returns the instance bound to [T] in this map, or `null` if none exists.
 * Note: unlike [ClassToInstanceMap.getInstance], there is no compile-time check that [T] is a
 * valid subtype of this map's base type `B`; an invalid [T] will simply return `null` at runtime.
 */
// Named `getTyped` to avoid shadowing by `Map.get(key)`, which takes priority in overload resolution.
// ClassToInstanceMap<B> extends Map<Class<? extends B>, B> (Java wildcard); Kotlin's type inference
// cannot match a concrete ClassToInstanceMap<Any> to ClassToInstanceMap<B>, so use star projection.
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> ClassToInstanceMap<*>.getTyped(): T? =
    (this as ClassToInstanceMap<Any>).getInstance(T::class.java)

inline fun <B : Any, reified T : B> ImmutableClassToInstanceMap.Builder<B>.put(value: T) =
    put(T::class.java, value)

inline fun <B : Any, reified T : B> MutableClassToInstanceMap<B>.put(value: T) =
    putInstance(T::class.java, value)

fun <B : Any> buildClassToInstanceMap(
    action: ImmutableClassToInstanceMap.Builder<B>.() -> Unit,
): ClassToInstanceMap<B> = ImmutableClassToInstanceMap.builder<B>().apply(action).build()

/**
 * Returns the instance bound to [T] in this map, or `null` if none exists.
 * Note: unlike [TypeToInstanceMap.getInstance], there is no compile-time check that [T] is a
 * valid subtype of this map's base type `B`; an invalid [T] will simply return `null` at runtime.
 */
// TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B> (Java wildcard), so Kotlin's type
// inference cannot match a concrete TypeToInstanceMap<Any> to TypeToInstanceMap<B>. Using a star
// projection with an explicit cast avoids the issue while keeping the reified type arg at call sites.
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> TypeToInstanceMap<*>.getTyped(): T? =
    (this as TypeToInstanceMap<Any>).getInstance(typeTokenOf<T>())

inline fun <B : Any, reified T : B> ImmutableTypeToInstanceMap.Builder<B>.put(value: T) =
    put(typeTokenOf(), value)

inline fun <B : Any, reified T : B> MutableTypeToInstanceMap<B>.put(value: T) =
    putInstance(typeTokenOf(), value)

fun <B : Any> buildTypeToInstanceMap(
    action: ImmutableTypeToInstanceMap.Builder<B>.() -> Unit,
): TypeToInstanceMap<B> = ImmutableTypeToInstanceMap.builder<B>().apply(action).build()
