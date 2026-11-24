package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure
import groovy.lang.GroovyObject

/*
 * Groovy dispatches a closure call to the public `doCall` methods of the closure's class, found by name, and derives the
 * closure's parameter count from them; `Closure` itself declares none. Hence one lambda adapter per arity, each with
 * exactly one `doCall` of that arity. The delegate-based closures declare two, so that callers may pass the target or not.
 */

/**
 * Maps Kotlin's [Unit] to `null`, so that Groovy never sees `kotlin.Unit` as a closure's result.
 */
internal fun unitToNull(value: Any?): Any? = value.takeUnless { it === Unit }

@Suppress("UNCHECKED_CAST")
internal class KotlinClosure0<V>(private val function: () -> V) : Closure<V?>(null) {
    fun doCall(): V? = unitToNull(function()) as V?
}

@Suppress("UNCHECKED_CAST")
internal class KotlinClosure1<P1, V>(private val function: (P1) -> V) : Closure<V?>(null) {
    fun doCall(p1: P1): V? = unitToNull(function(p1)) as V?
}

@Suppress("UNCHECKED_CAST")
internal class KotlinClosure2<P1, P2, V>(private val function: (P1, P2) -> V) : Closure<V?>(null) {
    fun doCall(p1: P1, p2: P2): V? = unitToNull(function(p1, p2)) as V?
}

@Suppress("UNCHECKED_CAST")
internal class KotlinClosure3<P1, P2, P3, V>(private val function: (P1, P2, P3) -> V) : Closure<V?>(null) {
    fun doCall(p1: P1, p2: P2, p3: P3): V? = unitToNull(function(p1, p2, p3)) as V?
}

/**
 * Runs [action] on the closure's delegate, resolving names against the delegate first. Callers differ in whether they pass
 * the target as an argument (`with` and configure-style helpers do), so both forms are accepted; when no delegate is set,
 * the argument is the target (as in `[server].each(closure)`).
 */
internal class KotlinDelegateClosure<T : Any>(
    private val type: Class<T>,
    private val action: T.() -> Unit,
) : Closure<Unit?>(null) {
    init {
        resolveStrategy = DELEGATE_FIRST
    }

    fun doCall(): Unit? = run(delegate ?: error("delegateClosureOf: no delegate set and no target passed"))

    fun doCall(target: Any?): Unit? =
        run(delegate ?: target ?: error("delegateClosureOf: no delegate set and a null target"))

    private fun run(receiver: Any): Unit? {
        type.cast(receiver).action()
        return null
    }
}

/**
 * A nested builder block: runs [block] in a [GroovyBuilderScope] over the delegate the builder sets; when it sets none,
 * over the argument the builder passes if that is a [GroovyObject], else over [enclosing] (as a Groovy closure resolves
 * through its owner). Returns the block's result to the builder.
 */
internal class KotlinBuilderClosure(
    private val enclosing: GroovyObject,
    private val block: GroovyBuilderScope.() -> Any?,
) : Closure<Any?>(null) {
    init {
        resolveStrategy = DELEGATE_FIRST
    }

    fun doCall(): Any? = run(null)

    fun doCall(argument: Any?): Any? = run(argument)

    private fun run(argument: Any?): Any? {
        val receiver = when (val target = delegate) {
            null -> argument as? GroovyObject ?: enclosing
            is GroovyObject -> target
            else -> error(
                "A Groovy builder block needs a GroovyObject delegate, but the builder set a ${target.javaClass.name}"
            )
        }
        return unitToNull(GroovyBuilderScope.of(receiver).block())
    }
}
