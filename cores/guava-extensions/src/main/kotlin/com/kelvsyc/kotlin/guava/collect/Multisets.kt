@file:Suppress("detekt:TooManyFunctions")
package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.EnumMultiset
import com.google.common.collect.HashMultiset
import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.LinkedHashMultiset
import com.google.common.collect.Multiset
import com.google.common.collect.TreeMultiset

fun <E : Any> buildMultiset(action: ImmutableMultiset.Builder<E>.() -> Unit): Multiset<E> =
    ImmutableMultiset.builder<E>().apply(action).build()

fun <E : Any> emptyMultiset(): Multiset<E> = ImmutableMultiset.of()

fun <E : Any> multisetOf(): Multiset<E> = ImmutableMultiset.of()

fun <E : Any> multisetOf(element: E): Multiset<E> = ImmutableMultiset.of(element)

fun <E : Any> multisetOf(e1: E, e2: E): Multiset<E> = ImmutableMultiset.of(e1, e2)

fun <E : Any> multisetOf(e1: E, e2: E, e3: E): Multiset<E> = ImmutableMultiset.of(e1, e2, e3)

fun <E : Any> multisetOf(e1: E, e2: E, e3: E, e4: E): Multiset<E> = ImmutableMultiset.of(e1, e2, e3, e4)

fun <E : Any> multisetOf(e1: E, e2: E, e3: E, e4: E, e5: E): Multiset<E> = ImmutableMultiset.of(e1, e2, e3, e4, e5)

fun <E : Any> multisetOf(vararg elements: E): Multiset<E> = buildMultiset { addAll(elements.asIterable()) }

fun <E : Any> Iterable<E>.toImmutableMultiset(): ImmutableMultiset<E> = ImmutableMultiset.copyOf(this)

inline fun <reified E : Enum<E>> enumMultisetOf(): EnumMultiset<E> = EnumMultiset.create(E::class.java)

inline fun <reified E : Enum<E>> enumMultisetOf(vararg elements: E): EnumMultiset<E> =
    EnumMultiset.create(elements.asIterable(), E::class.java)

fun <E> hashMultisetOf(): HashMultiset<E> = HashMultiset.create()

fun <E> hashMultisetOf(vararg elements: E): HashMultiset<E> = HashMultiset.create(elements.asIterable())

fun <E> linkedMultisetOf(): LinkedHashMultiset<E> = LinkedHashMultiset.create()

fun <E> linkedMultisetOf(vararg elements: E): LinkedHashMultiset<E> = LinkedHashMultiset.create(elements.asIterable())

fun <E : Comparable<E>> treeMultisetOf(): TreeMultiset<E> = TreeMultiset.create()

fun <E> treeMultisetOf(comparator: Comparator<in E>): TreeMultiset<E> = TreeMultiset.create(comparator)

fun <E : Comparable<E>> treeMultisetOf(vararg elements: E): TreeMultiset<E> =
    TreeMultiset.create(elements.asIterable())

fun <E> treeMultisetOf(comparator: Comparator<in E>, vararg elements: E): TreeMultiset<E> =
    treeMultisetOf(comparator).also { it.addAll(elements.asList()) }
