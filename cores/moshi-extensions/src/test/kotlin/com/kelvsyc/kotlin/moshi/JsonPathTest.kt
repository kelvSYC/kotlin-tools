package com.kelvsyc.kotlin.moshi

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class JsonPathTest : FunSpec({

    val store = JsonObject(
        mapOf(
            "store" to JsonObject(
                mapOf(
                    "book" to JsonArray(
                        listOf(
                            JsonObject(
                                mapOf(
                                    "category" to JsonString("reference"),
                                    "author" to JsonString("Nigel Rees"),
                                    "title" to JsonString("Sayings of the Century"),
                                    "price" to JsonNumber(8.95),
                                )
                            ),
                            JsonObject(
                                mapOf(
                                    "category" to JsonString("fiction"),
                                    "author" to JsonString("Evelyn Waugh"),
                                    "title" to JsonString("Sword of Honour"),
                                    "price" to JsonNumber(12.99),
                                )
                            ),
                            JsonObject(
                                mapOf(
                                    "category" to JsonString("fiction"),
                                    "author" to JsonString("Herman Melville"),
                                    "title" to JsonString("Moby Dick"),
                                    "price" to JsonNumber(8.99),
                                )
                            ),
                        )
                    ),
                    "bicycle" to JsonObject(
                        mapOf(
                            "color" to JsonString("red"),
                            "price" to JsonNumber(19.95),
                        )
                    ),
                )
            ),
        )
    )

    context("parsing") {
        test("root only") {
            val path = JsonPath.parse("$")
            path.segments.shouldBeEmpty()
        }

        test("dot notation") {
            val path = JsonPath.parse("$.store.book")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.Key("store"),
                JsonPathSegment.Key("book"),
            )
        }

        test("bracket notation with index") {
            val path = JsonPath.parse("$.store.book[0]")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.Key("store"),
                JsonPathSegment.Key("book"),
                JsonPathSegment.Index(0),
            )
        }

        test("bracket notation with quoted key") {
            val path = JsonPath.parse("$['store']['book']")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.Key("store"),
                JsonPathSegment.Key("book"),
            )
        }

        test("wildcard with dot") {
            val path = JsonPath.parse("$.store.*")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.Key("store"),
                JsonPathSegment.Wildcard,
            )
        }

        test("wildcard with bracket") {
            val path = JsonPath.parse("$.store[*]")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.Key("store"),
                JsonPathSegment.Wildcard,
            )
        }

        test("recursive descent") {
            val path = JsonPath.parse("$..author")
            path.segments shouldContainExactly listOf(
                JsonPathSegment.RecursiveDescent,
                JsonPathSegment.Key("author"),
            )
        }

        test("rejects expression not starting with dollar") {
            shouldThrow<IllegalArgumentException> {
                JsonPath.parse("store.book")
            }
        }

        test("toString round-trips") {
            val expressions = listOf(
                "$",
                "$.store.book",
                "$.store.book[0].title",
                "$.*",
                "$..author",
            )
            for (expr in expressions) {
                JsonPath.parse(expr).toString() shouldBe expr
            }
        }
    }

    context("query") {
        test("root returns the whole document") {
            JsonPath.parse("$").query(store) shouldContainExactly listOf(store)
        }

        test("dot notation navigates objects") {
            val results = JsonPath.parse("$.store.bicycle.color").query(store)
            results shouldContainExactly listOf(JsonString("red"))
        }

        test("array index") {
            val results = JsonPath.parse("$.store.book[0].title").query(store)
            results shouldContainExactly listOf(JsonString("Sayings of the Century"))
        }

        test("wildcard on array") {
            val results = JsonPath.parse("$.store.book[*].author").query(store)
            results shouldContainExactly listOf(
                JsonString("Nigel Rees"),
                JsonString("Evelyn Waugh"),
                JsonString("Herman Melville"),
            )
        }

        test("wildcard on object") {
            val results = JsonPath.parse("$.store.*").query(store)
            results.size shouldBe 2
        }

        test("recursive descent finds all matching keys") {
            val results = JsonPath.parse("$..price").query(store)
            results shouldContainExactly listOf(
                JsonNumber(8.95),
                JsonNumber(12.99),
                JsonNumber(8.99),
                JsonNumber(19.95),
            )
        }

        test("recursive descent with wildcard") {
            val results = JsonPath.parse("$..book[*].title").query(store)
            results shouldContainExactly listOf(
                JsonString("Sayings of the Century"),
                JsonString("Sword of Honour"),
                JsonString("Moby Dick"),
            )
        }

        test("missing key returns empty") {
            JsonPath.parse("$.store.missing").query(store).shouldBeEmpty()
        }

        test("out-of-bounds index returns empty") {
            JsonPath.parse("$.store.book[99]").query(store).shouldBeEmpty()
        }

        test("key on scalar returns empty") {
            JsonPath.parse("$.store.bicycle.color.length").query(store).shouldBeEmpty()
        }
    }

    context("queryOne") {
        test("returns single match") {
            JsonPath.parse("$.store.bicycle.color").queryOne(store) shouldBe JsonString("red")
        }

        test("returns null for no match") {
            JsonPath.parse("$.store.missing").queryOne(store).shouldBeNull()
        }

        test("returns null for multiple matches") {
            JsonPath.parse("$..price").queryOne(store).shouldBeNull()
        }
    }

    context("extension functions") {
        test("query extension parses and evaluates") {
            store.query("$..author") shouldContainExactly listOf(
                JsonString("Nigel Rees"),
                JsonString("Evelyn Waugh"),
                JsonString("Herman Melville"),
            )
        }

        test("queryOne extension parses and evaluates") {
            store.queryOne("$.store.bicycle.color") shouldBe JsonString("red")
        }
    }
})
