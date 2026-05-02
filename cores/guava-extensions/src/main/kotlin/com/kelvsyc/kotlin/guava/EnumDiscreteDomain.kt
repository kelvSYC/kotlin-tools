package com.kelvsyc.kotlin.guava

import com.google.common.collect.DiscreteDomain
import java.io.Serializable

class EnumDiscreteDomain<E : Enum<E>>(constants: Array<E>) : DiscreteDomain<E>(), Serializable {
    constructor(enumClass: Class<E>) : this(
        checkNotNull(enumClass.enumConstants) { "${enumClass.name} is not an enum type" }
    )

    init {
        require(constants.isNotEmpty()) { "Enum type has no constants" }
    }

    private val constants: Array<E> = constants

    override fun next(value: E): E? = constants.getOrNull(value.ordinal + 1)
    override fun previous(value: E): E? = constants.getOrNull(value.ordinal - 1)
    override fun distance(start: E, end: E): Long = (end.ordinal - start.ordinal).toLong()
    override fun minValue(): E = constants.first()
    override fun maxValue(): E = constants.last()
    override fun toString(): String =
        "DiscreteDomain.forEnum(${(constants.first() as java.lang.Enum<*>).declaringClass.simpleName})"
}

inline fun <reified E : Enum<E>> enumDiscreteDomain(): EnumDiscreteDomain<E> = EnumDiscreteDomain(enumValues<E>())
