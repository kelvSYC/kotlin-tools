package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.Bag
import org.apache.commons.collections4.bag.HashBag
import org.apache.commons.collections4.bag.TreeBag

fun <E> hashBagOf(): HashBag<E> = HashBag()

fun <E> hashBagOf(vararg elements: E): HashBag<E> = HashBag<E>().also { bag ->
    elements.forEach { bag.add(it) }
}

fun <E> treeBagOf(): TreeBag<E> = TreeBag()

fun <E> treeBagOf(vararg elements: E): TreeBag<E> = TreeBag<E>().also { bag ->
    elements.forEach { bag.add(it) }
}

fun <E> buildBag(builderAction: Bag<E>.() -> Unit): Bag<E> = HashBag<E>().also { it.builderAction() }

fun <E> buildTreeBag(builderAction: Bag<E>.() -> Unit): Bag<E> = TreeBag<E>().also { it.builderAction() }

operator fun <E> Bag<E>.plusAssign(element: E) { add(element) }

operator fun <E> Bag<E>.plusAssign(elements: Bag<E>) { addAll(elements) }

operator fun <E> Bag<E>.minusAssign(element: E) { remove(element, 1) }

operator fun <E> Bag<E>.minusAssign(elements: Bag<E>) { removeAll(elements) }
