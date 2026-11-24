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
