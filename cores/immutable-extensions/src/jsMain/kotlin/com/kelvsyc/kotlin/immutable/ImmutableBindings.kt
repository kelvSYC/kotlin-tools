package com.kelvsyc.kotlin.immutable

// external interface (not external class) — avoids instanceof checks in generic bridge methods.
// Immutable.js types are named exports (require('immutable').List), not globals, so instanceof
// checks would produce ReferenceError at runtime. external interface uses duck typing instead.

external interface ImmutableList<T> {
    val size: Int
    fun isEmpty(): Boolean
    fun get(index: Int): T?
    fun has(index: Int): Boolean
    fun first(): T?
    fun last(): T?
    fun set(index: Int, value: T): ImmutableList<T>
    fun push(vararg values: T): ImmutableList<T>
    fun pop(): ImmutableList<T>
    fun unshift(vararg values: T): ImmutableList<T>
    fun shift(): ImmutableList<T>
    fun insert(index: Int, value: T): ImmutableList<T>
    fun delete(index: Int): ImmutableList<T>
    fun includes(value: T): Boolean
    fun indexOf(value: T): Int
    fun lastIndexOf(value: T): Int
    fun reverse(): ImmutableList<T>
    fun sort(comparator: dynamic = definedExternally): ImmutableList<T>
    fun sortBy(keyExtractor: dynamic, comparator: dynamic = definedExternally): ImmutableList<T>
    fun slice(begin: Int = definedExternally, end: Int = definedExternally): ImmutableList<T>
    fun concat(other: dynamic): ImmutableList<T>
    fun filter(predicate: (value: T) -> Boolean): ImmutableList<T>
    fun find(predicate: (value: T) -> Boolean): T?
    fun findIndex(predicate: (value: T) -> Boolean): Int
    fun forEach(sideEffect: (value: T) -> Unit)
    fun map(mapper: (value: T) -> dynamic): ImmutableList<*>
    fun flatMap(mapper: (value: T) -> dynamic): ImmutableList<*>
    fun reduce(reducer: (reduction: dynamic, value: T) -> dynamic): dynamic
    fun count(): Int
    fun toArray(): Array<T>
    fun toJS(): dynamic
    @JsName("equals") fun immutableEquals(other: Any?): Boolean
    @JsName("hashCode") fun immutableHashCode(): Int
}

external interface ImmutableMap<K, V> {
    val size: Int
    fun isEmpty(): Boolean
    fun get(key: K): V?
    fun has(key: K): Boolean
    fun set(key: K, value: V): ImmutableMap<K, V>
    fun delete(key: K): ImmutableMap<K, V>
    fun remove(key: K): ImmutableMap<K, V>
    fun merge(other: ImmutableMap<K, V>): ImmutableMap<K, V>
    fun mergeWith(merger: (oldVal: V, newVal: V, key: K) -> V, other: ImmutableMap<K, V>): ImmutableMap<K, V>
    fun update(key: K, updater: (value: V?) -> V): ImmutableMap<K, V>
    fun filter(predicate: (value: V, key: K) -> Boolean): ImmutableMap<K, V>
    fun map(mapper: (value: V, key: K) -> dynamic): ImmutableMap<K, *>
    fun sortBy(keyExtractor: dynamic, comparator: dynamic = definedExternally): ImmutableMap<K, V>
    fun forEach(sideEffect: (value: V, key: K) -> Unit)
    fun find(predicate: (value: V, key: K) -> Boolean): V?
    fun count(): Int
    fun keys(): dynamic
    fun values(): dynamic
    fun entries(): dynamic
    fun toJS(): dynamic
    @JsName("equals") fun immutableEquals(other: Any?): Boolean
    @JsName("hashCode") fun immutableHashCode(): Int
}

external interface ImmutableSet<T> {
    val size: Int
    fun isEmpty(): Boolean
    fun has(value: T): Boolean
    fun includes(value: T): Boolean
    fun add(value: T): ImmutableSet<T>
    fun delete(value: T): ImmutableSet<T>
    fun remove(value: T): ImmutableSet<T>
    fun union(other: ImmutableSet<T>): ImmutableSet<T>
    fun intersect(other: ImmutableSet<T>): ImmutableSet<T>
    fun subtract(other: ImmutableSet<T>): ImmutableSet<T>
    fun filter(predicate: (value: T) -> Boolean): ImmutableSet<T>
    fun map(mapper: (value: T) -> dynamic): ImmutableSet<*>
    fun sortBy(keyExtractor: dynamic, comparator: dynamic = definedExternally): ImmutableSet<T>
    fun forEach(sideEffect: (value: T) -> Unit)
    fun find(predicate: (value: T) -> Boolean): T?
    fun count(): Int
    fun toArray(): Array<T>
    fun toJS(): dynamic
    @JsName("equals") fun immutableEquals(other: Any?): Boolean
    @JsName("hashCode") fun immutableHashCode(): Int
}

// Named exports from the immutable package. dynamic properties are callable as factory functions
// (Immutable.js factories do not require `new`).
@JsModule("immutable")
@JsNonModule
internal external object ImmutableModule {
    val List: dynamic
    val Map: dynamic
    val Set: dynamic
    @JsName("is") fun structurallyEqual(a: dynamic, b: dynamic): Boolean
}
