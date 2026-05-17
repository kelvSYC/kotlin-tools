package com.kelvsyc.kotlin.immutable

import com.kelvsyc.kotlin.core.traits.ValueEquality

// Immutable.js has deep structural equality: two different instances with the same contents
// are .equals(). ImmutableModule.structurallyEqual(a, b) (JS `is(a, b)`) extends this across
// collection types. All three instances delegate to .equals() on the instance.

private val immutableListInstance: ValueEquality<ImmutableList<*>> = object : ValueEquality<ImmutableList<*>> {
    override fun ImmutableList<*>.isEqualTo(other: ImmutableList<*>): Boolean = immutableEquals(other)
}

private val immutableMapInstance: ValueEquality<ImmutableMap<*, *>> = object : ValueEquality<ImmutableMap<*, *>> {
    override fun ImmutableMap<*, *>.isEqualTo(other: ImmutableMap<*, *>): Boolean = immutableEquals(other)
}

private val immutableSetInstance: ValueEquality<ImmutableSet<*>> = object : ValueEquality<ImmutableSet<*>> {
    override fun ImmutableSet<*>.isEqualTo(other: ImmutableSet<*>): Boolean = immutableEquals(other)
}

val ValueEquality.Companion.immutableList: ValueEquality<ImmutableList<*>> get() = immutableListInstance
val ValueEquality.Companion.immutableMap: ValueEquality<ImmutableMap<*, *>> get() = immutableMapInstance
val ValueEquality.Companion.immutableSet: ValueEquality<ImmutableSet<*>> get() = immutableSetInstance
