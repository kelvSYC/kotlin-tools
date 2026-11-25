package com.kelvsyc.kotlin.groovy.sql

/**
 * The receiver of a [SqlDatabase.batch] block: each [add] queues one execution of the batch's statement. Every added
 * query must have the same SQL text (only its bound values differ), since a batch runs one prepared statement.
 */
class SqlBatch internal constructor() {
    internal var text: String? = null
        private set
    internal val parameterSets = mutableListOf<List<Any?>>()

    /**
     * Queues the query built by [template] (see [query]).
     */
    fun add(template: SqlQueryScope.() -> String) = add(query(template))

    /**
     * Queues [query], which must have the same SQL text as every other query in this batch.
     */
    fun add(query: SqlQuery) {
        val expected = text
        if (expected == null) {
            text = query.text
        } else {
            require(query.text == expected) {
                "batch: every added query must be the same statement, but `${query.text}` follows `$expected`"
            }
        }
        parameterSets += query.parameters
    }
}
