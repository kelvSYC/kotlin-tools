package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class TriesTest : FunSpec({

    context("patriciaTrie") {
        test("empty trie") {
            val trie = patriciaTrie<Int>()
            trie.size shouldBe 0
        }
        test("trie with pairs") {
            val trie = patriciaTrie("apple" to 1, "app" to 2, "banana" to 3)
            trie["apple"] shouldBe 1
            trie["app"] shouldBe 2
        }
    }

    context("buildPatriciaTrie") {
        test("builder actions are applied") {
            val trie = buildPatriciaTrie<Int> {
                put("cat", 1)
                put("car", 2)
            }
            trie.size shouldBe 2
            trie["cat"] shouldBe 1
        }
    }

    context("keysWithPrefix") {
        test("returns all keys matching prefix") {
            val trie = patriciaTrie("apple" to 1, "app" to 2, "application" to 3, "banana" to 4)
            trie.keysWithPrefix("app") shouldContainExactlyInAnyOrder listOf("apple", "app", "application")
        }
        test("no matches returns empty set") {
            val trie = patriciaTrie("apple" to 1)
            trie.keysWithPrefix("xyz").size shouldBe 0
        }
    }

    context("entriesWithPrefix") {
        test("returns entries matching prefix") {
            val trie = patriciaTrie("cat" to 10, "car" to 20, "dog" to 30)
            val entries = trie.entriesWithPrefix("ca")
            entries.map { it.key } shouldContainExactlyInAnyOrder listOf("cat", "car")
            entries.map { it.value } shouldContainExactlyInAnyOrder listOf(10, 20)
        }
    }
})
