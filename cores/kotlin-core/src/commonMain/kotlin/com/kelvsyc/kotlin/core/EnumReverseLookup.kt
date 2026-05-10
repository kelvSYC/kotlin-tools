package com.kelvsyc.kotlin.core

import kotlin.enums.enumEntries
inline fun <reified E : Enum<E>, K> enumReverseLookup(
    crossinline keyExtractor: (E) -> K,
): Lazy<Map<K, E>> = lazy { enumEntries<E>().associateBy(keyExtractor) }
