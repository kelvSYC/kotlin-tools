package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.ImmutableSortedMultiset
import com.google.common.collect.SortedMultiset

fun <E : Comparable<E>> buildSortedMultiset(action: ImmutableSortedMultiset.Builder<E>.() -> Unit): SortedMultiset<E> =
    ImmutableSortedMultiset.naturalOrder<E>().apply(action).build()

fun <E : Any> buildSortedMultiset(
    comparator: Comparator<in E>,
    action: ImmutableSortedMultiset.Builder<E>.() -> Unit,
): SortedMultiset<E> = ImmutableSortedMultiset.Builder(comparator).apply(action).build()

fun <E : Any> emptySortedMultiset(): SortedMultiset<E> = ImmutableSortedMultiset.of()

fun <E : Any> sortedMultisetOf(): SortedMultiset<E> = ImmutableSortedMultiset.of()

fun <E : Comparable<E>> sortedMultisetOf(vararg elements: E): SortedMultiset<E> =
    ImmutableSortedMultiset.copyOf(naturalOrder(), elements.asIterable())

fun <E : Any> sortedMultisetOf(comparator: Comparator<in E>, vararg elements: E): SortedMultiset<E> =
    ImmutableSortedMultiset.copyOf(comparator, elements.asIterable())
