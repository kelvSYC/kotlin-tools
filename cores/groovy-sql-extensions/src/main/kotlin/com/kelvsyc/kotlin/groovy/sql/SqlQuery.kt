package com.kelvsyc.kotlin.groovy.sql

import com.kelvsyc.kotlin.groovy.GStringScope
import com.kelvsyc.kotlin.groovy.gstring
import groovy.lang.GString
import groovy.sql.ExpandedVariable

private const val EXPANDED_VARIABLE_MESSAGE =
    "Sql.expand(…) values are pasted into the SQL text, not bound; " +
        "use trustedSql(…) for SQL text you build yourself"

/**
 * A query for a [SqlDatabase]. There is deliberately no way to make one from a plain `String` other than [sql] (with
 * `?` placeholders) and [trustedSql] (explicitly trusted text), so values can't be pasted into SQL by accident. Build
 * one with [query].
 */
class SqlQuery internal constructor(internal val form: Form) {
    /**
     * The SQL text, with a `?` placeholder for each bound parameter; for logging and diagnostics.
     */
    val text: String
        get() = when (form) {
            is Form.Template -> form.gstring.strings.joinToString("?")
            is Form.Text -> form.text
        }

    /**
     * The bound parameters, in placeholder order.
     */
    val parameters: List<Any?>
        get() = when (form) {
            is Form.Template -> form.gstring.values.toList()
            is Form.Text -> form.parameters
        }

    override fun toString(): String = "SqlQuery($text, $parameters)"

    internal sealed interface Form {
        /** A template whose values `Sql` binds as parameters. */
        data class Template(val gstring: GString) : Form

        /** Text with `?` placeholders and their parameters. */
        data class Text(val text: String, val parameters: List<Any?>) : Form
    }
}

/**
 * The receiver of a [query] block: [param] marks where a bound value goes.
 */
class SqlQueryScope internal constructor(private val scope: GStringScope) {
    /**
     * Binds [value] as a query parameter, returning the placeholder to interpolate into the query text. A collection or
     * an array (other than a `ByteArray`, which binds as one binary value) expands to one parameter per element, for
     * `in (…)` lists; it must not be empty. `null` binds SQL `NULL`. Values from `Sql.expand(…)` are rejected: they are
     * pasted into the SQL text rather than bound, so use [trustedSql] for text you build yourself.
     */
    fun param(value: Any?): String = when (value) {
        is Iterable<*> -> expand(value.toList())
        is Array<*> -> expand(value.toList())
        is IntArray -> expand(value.toList())
        is LongArray -> expand(value.toList())
        is ShortArray -> expand(value.toList())
        is DoubleArray -> expand(value.toList())
        is FloatArray -> expand(value.toList())
        is BooleanArray -> expand(value.toList())
        is CharArray -> expand(value.toList())
        else -> scope.value(checkBindable(value))
    }

    private fun expand(values: List<Any?>): String {
        require(values.isNotEmpty()) { "param: an empty collection can't be expanded (`in ()` is not valid SQL)" }
        return values.joinToString(", ") { scope.value(checkBindable(it)) }
    }
}

/**
 * Builds a query from a Kotlin template, with [SqlQueryScope.param] marking each bound value:
 * `query { "select * from books where author = ${param(author)}" }`. The values are bound as parameters, never pasted
 * into the SQL text.
 */
fun query(block: SqlQueryScope.() -> String): SqlQuery =
    SqlQuery(SqlQuery.Form.Template(gstring { SqlQueryScope(this).block() }))

/**
 * A query from [text] with `?` placeholders and their [parameters], bound one per placeholder (collections are not
 * expanded). [text] should be a constant: never build it from untrusted input.
 */
fun sql(text: String, vararg parameters: Any?): SqlQuery =
    SqlQuery(SqlQuery.Form.Text(text, parameters.map(::checkBindable)))

/**
 * A query from [text] with no parameters, for SQL text you build yourself (table names, generated clauses). [text] must
 * never contain untrusted input: nothing in it is bound.
 */
fun trustedSql(text: String): SqlQuery = SqlQuery(SqlQuery.Form.Text(text, emptyList()))

private fun checkBindable(value: Any?): Any? {
    require(value !is ExpandedVariable) { EXPANDED_VARIABLE_MESSAGE }
    return value
}
