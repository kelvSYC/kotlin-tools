package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.ImmutableRangeSet
import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet

fun <C : Comparable<C>> buildRangeSet(action: ImmutableRangeSet.Builder<C>.() -> Unit): RangeSet<C> =
    ImmutableRangeSet.builder<C>().apply(action).build()

fun <C : Comparable<C>> emptyRangeSet(): RangeSet<C> = ImmutableRangeSet.of()

fun <C : Comparable<C>> rangeSetOf(): RangeSet<C> = ImmutableRangeSet.of()

fun <C : Comparable<C>> rangeSetOf(element: Range<C>): RangeSet<C> = ImmutableRangeSet.of(element)

fun <C : Comparable<C>> rangeSetOf(vararg elements: Range<C>): RangeSet<C> =
    ImmutableRangeSet.unionOf(elements.asIterable())

fun <C : Comparable<C>> Iterable<Range<C>>.toImmutableRangeSet(): RangeSet<C> =
    ImmutableRangeSet.unionOf(this)

fun <C : Comparable<C>> treeRangeSetOf(): TreeRangeSet<C> = TreeRangeSet.create()

fun <C : Comparable<C>> treeRangeSetOf(vararg elements: Range<C>): TreeRangeSet<C> =
    TreeRangeSet.create(elements.asIterable())
