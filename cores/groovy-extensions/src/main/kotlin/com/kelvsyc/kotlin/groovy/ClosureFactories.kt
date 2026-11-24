package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure

/**
 * Adapts a Kotlin function of no parameters to a Groovy [Closure] taking none. A [Unit] result is returned to Groovy as
 * `null`.
 */
fun <V> closureOf(function: () -> V): Closure<V?> = KotlinClosure0(function)

/**
 * Adapts a Kotlin function of one parameter to a Groovy [Closure] taking one, as seen by Groovy APIs that inspect a
 * closure's parameter count. A [Unit] result is returned to Groovy as `null`.
 */
fun <P1, V> closureOf(function: (P1) -> V): Closure<V?> = KotlinClosure1(function)

/**
 * Adapts a Kotlin function of two parameters to a Groovy [Closure] taking two, as seen by Groovy APIs that inspect a
 * closure's parameter count. A [Unit] result is returned to Groovy as `null`.
 */
fun <P1, P2, V> closureOf(function: (P1, P2) -> V): Closure<V?> = KotlinClosure2(function)

/**
 * Adapts a Kotlin function of three parameters to a Groovy [Closure] taking three, as seen by Groovy APIs that inspect a
 * closure's parameter count. A [Unit] result is returned to Groovy as `null`.
 */
fun <P1, P2, P3, V> closureOf(function: (P1, P2, P3) -> V): Closure<V?> = KotlinClosure3(function)

/**
 * Adapts a Kotlin receiver lambda to a Groovy [Closure] that runs [action] on its delegate, resolving names against the
 * delegate first, for configuration-style Groovy APIs. The closure accepts being called with no arguments or with the
 * target as its argument. Calling it when the delegate is not a [T] throws [ClassCastException].
 */
fun <T : Any> delegateClosureOf(type: Class<T>, action: T.() -> Unit): Closure<Unit?> = KotlinDelegateClosure(type, action)

/**
 * Adapts a Kotlin receiver lambda to a Groovy [Closure] that runs [action] on its delegate; see the [Class]-taking
 * overload.
 */
inline fun <reified T : Any> delegateClosureOf(noinline action: T.() -> Unit): Closure<Unit?> =
    delegateClosureOf(T::class.java, action)
