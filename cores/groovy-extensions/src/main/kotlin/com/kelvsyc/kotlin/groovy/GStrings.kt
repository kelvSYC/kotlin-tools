package com.kelvsyc.kotlin.groovy

import groovy.lang.GString
import java.util.concurrent.atomic.AtomicLong

private const val MARKER_START = ''
private const val MARKER_END = ''
private val MARKER = Regex("$MARKER_START(\\d+):(\\d+)$MARKER_END")
private val scopeIds = AtomicLong()

/**
 * The receiver of a [gstring] block: [value] marks where a value goes in the template.
 */
class GStringScope internal constructor() {
    private val id = scopeIds.incrementAndGet()
    internal val values = mutableListOf<Any?>()

    /**
     * Records [value] and returns a marker to interpolate into the template where the value belongs. The marker is only
     * meaningful in the text returned by this scope's block.
     */
    fun value(value: Any?): String {
        values += value
        return "$MARKER_START$id:${values.size - 1}$MARKER_END"
    }

    internal fun build(text: String): GString {
        val strings = mutableListOf<String>()
        val ordered = mutableListOf<Any?>()
        val used = BooleanArray(values.size)
        var start = 0
        for (match in MARKER.findAll(text)) {
            val (scope, index) = match.destructured
            check(scope.toLong() == id) { "gstring: a value marker from another gstring builder was used" }
            val i = index.toInt()
            check(!used[i]) { "gstring: the marker of a value was used more than once" }
            used[i] = true
            strings += text.substring(start, match.range.first)
            ordered += values[i]
            start = match.range.last + 1
        }
        strings += text.substring(start)
        check(used.all { it }) { "gstring: a recorded value's marker is missing from the returned text" }
        return KotlinGString(ordered.toTypedArray(), strings.toTypedArray())
    }
}

/**
 * Builds a Groovy [GString] from a Kotlin template: interpolate `value(x)` wherever a value belongs, and every such
 * value becomes one of the GString's values rather than part of its text, as in Groovy's `"… ${x} …"`, for Groovy APIs
 * that treat a GString's values differently from its text.
 *
 * Throws [IllegalStateException] when a recorded value's marker is missing from the returned text, used twice, or comes
 * from another `gstring` block.
 *
 * Don't use it to build queries for `groovy.sql.Sql`. `Sql` doesn't bind every value of a GString: before Groovy 6
 * it pastes a value written between quotes (`'${value(x)}'`) into the SQL text, which is an injection risk, and it
 * rewrites `= ${value(null)}` to `is null`. Use `groovy-sql-extensions`, which always binds, instead.
 */
fun gstring(block: GStringScope.() -> String): GString {
    val scope = GStringScope()
    return scope.build(scope.block())
}

private class KotlinGString(values: Array<Any?>, private val parts: Array<String>) : GString(values) {
    override fun getStrings(): Array<String> = parts
}
