package com.kelvsyc.kotlin.groovy

import groovy.lang.Closure
import groovy.lang.GroovyObject

/**
 * Marks Groovy builder scopes, so that a nested block can't implicitly call an outer scope's members, which would send
 * them to the wrong delegate. Scopes implementing [GroovyBuilderScope] inherit it; annotate other receiver types used
 * inside builder blocks with it.
 */
@DslMarker
annotation class GroovyBuilderDsl

/**
 * Drives a Groovy builder from Kotlin with Groovy's calling conventions: `"name"(arguments) { nested block }`.
 *
 * Every `"key" to value` argument (a [Pair] with a `String` key), wherever it is written, is gathered (in order) into
 * one map passed first, as Groovy passes named arguments; the other arguments follow in order, then the nested block.
 * Such a `Pair` therefore can't be passed as an ordinary argument.
 *
 * Typed scopes for specific builders can be built by delegation: `class AntScope(s: GroovyBuilderScope) :
 * GroovyBuilderScope by s`. They inherit the [GroovyBuilderDsl] marker from this interface.
 *
 * Implementations outside this library are expected, so this interface's members are kept stable: the library adds new
 * capabilities as extension functions on [GroovyBuilderScope], never as members, which could break implementations or
 * silently take precedence over their own extensions.
 */
@GroovyBuilderDsl
interface GroovyBuilderScope {
    /**
     * The object this scope's calls go to.
     */
    val delegate: GroovyObject

    /**
     * Calls the method named by this string on [delegate], returning what the builder returns.
     */
    operator fun String.invoke(vararg arguments: Any?): Any? =
        delegate.invokeMethod(this, groovyArguments(arguments, null))

    /**
     * Calls the method named by this string on [delegate] with [block] as a nested-block closure after the other
     * arguments, returning what the builder returns. The block runs in a scope over the delegate the builder sets on
     * the closure; when it sets none, over the closure's argument if that is a `GroovyObject`, else over this scope's
     * delegate.
     */
    operator fun <T> String.invoke(vararg arguments: Any?, block: GroovyBuilderScope.() -> T): Any? =
        delegate.invokeMethod(this, groovyArguments(arguments, KotlinBuilderClosure(delegate, block)))

    /**
     * Reads the property [name] of [delegate].
     */
    operator fun get(name: String): Any? = delegate.getProperty(name)

    /**
     * Writes the property [name] of [delegate].
     */
    operator fun set(name: String, value: Any?) = delegate.setProperty(name, value)

    companion object {
        /**
         * Returns a scope whose calls go to [delegate].
         */
        fun of(delegate: GroovyObject): GroovyBuilderScope = DefaultGroovyBuilderScope(delegate)
    }
}

/**
 * Runs [block] in a [GroovyBuilderScope] over this object, returning the block's result. Like Kotlin's scope functions
 * it is inline, so the block can `return` from the enclosing function; nested blocks can't, since the builder runs them
 * as Groovy closures.
 */
inline fun <T> GroovyObject.withGroovyBuilder(block: GroovyBuilderScope.() -> T): T =
    GroovyBuilderScope.of(this).block()

private class DefaultGroovyBuilderScope(override val delegate: GroovyObject) : GroovyBuilderScope

/**
 * Arranges [arguments] as Groovy passes them: named arguments (every `Pair` with a `String` key) as one leading map,
 * then the positional arguments, then [block] if present.
 */
internal fun groovyArguments(arguments: Array<out Any?>, block: Closure<*>?): Array<Any?> {
    val named = LinkedHashMap<String, Any?>()
    val positional = ArrayList<Any?>()
    for (argument in arguments) {
        val key = (argument as? Pair<*, *>)?.first
        if (key is String) named[key] = argument.second else positional += argument
    }
    return buildList {
        if (named.isNotEmpty()) add(named)
        addAll(positional)
        if (block != null) add(block)
    }.toTypedArray()
}
