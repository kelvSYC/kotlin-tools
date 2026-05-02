package com.kelvsyc.kotlin.commons.lang.tuple

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.ImmutableTriple
import org.apache.commons.lang3.tuple.MutablePair
import org.apache.commons.lang3.tuple.MutableTriple
import org.apache.commons.lang3.tuple.Pair as CommonsPair
import org.apache.commons.lang3.tuple.Triple as CommonsTriple

class PairExtensionsTest : FunSpec({

    // ── Pair conversions ──────────────────────────────────────────────────────

    context("Pair.toImmutablePair") {
        test("produces an ImmutablePair with matching elements") {
            val a = Any(); val b = Any()
            val result = (a to b).toImmutablePair()
            result.shouldBeInstanceOf<ImmutablePair<*, *>>()
            result.left shouldBeSameInstanceAs a
            result.right shouldBeSameInstanceAs b
        }
    }

    context("Pair.toMutablePair") {
        test("produces a MutablePair with matching elements") {
            val a = Any(); val b = Any()
            val result = (a to b).toMutablePair()
            result.shouldBeInstanceOf<MutablePair<*, *>>()
            result.left shouldBeSameInstanceAs a
            result.right shouldBeSameInstanceAs b
        }
        test("mutating the MutablePair does not affect the original Kotlin Pair") {
            val original = "hello" to "world"
            val mutable = original.toMutablePair()
            mutable.left = "changed"
            original.first shouldBe "hello"
            mutable.left shouldBe "changed"
        }
    }

    context("CommonsPair.toKotlinPair") {
        test("converts an ImmutablePair") {
            val a = Any(); val b = Any()
            val result = ImmutablePair.of(a, b).toKotlinPair()
            result.first shouldBeSameInstanceAs a
            result.second shouldBeSameInstanceAs b
        }
        test("converts a MutablePair") {
            val a = Any(); val b = Any()
            val result = MutablePair.of(a, b).toKotlinPair()
            result.first shouldBeSameInstanceAs a
            result.second shouldBeSameInstanceAs b
        }
    }

    // ── Triple conversions ────────────────────────────────────────────────────

    context("Triple.toImmutableTriple") {
        test("produces an ImmutableTriple with matching elements") {
            val a = Any(); val b = Any(); val c = Any()
            val result = Triple(a, b, c).toImmutableTriple()
            result.shouldBeInstanceOf<ImmutableTriple<*, *, *>>()
            result.left shouldBeSameInstanceAs a
            result.middle shouldBeSameInstanceAs b
            result.right shouldBeSameInstanceAs c
        }
    }

    context("Triple.toMutableTriple") {
        test("produces a MutableTriple with matching elements") {
            val a = Any(); val b = Any(); val c = Any()
            val result = Triple(a, b, c).toMutableTriple()
            result.shouldBeInstanceOf<MutableTriple<*, *, *>>()
            result.left shouldBeSameInstanceAs a
            result.middle shouldBeSameInstanceAs b
            result.right shouldBeSameInstanceAs c
        }
        test("mutating the MutableTriple does not affect the original Kotlin Triple") {
            val original = Triple("a", "b", "c")
            val mutable = original.toMutableTriple()
            mutable.middle = "changed"
            original.second shouldBe "b"
            mutable.middle shouldBe "changed"
        }
    }

    context("CommonsTriple.toKotlinTriple") {
        test("converts an ImmutableTriple") {
            val a = Any(); val b = Any(); val c = Any()
            val result = ImmutableTriple.of(a, b, c).toKotlinTriple()
            result.first shouldBeSameInstanceAs a
            result.second shouldBeSameInstanceAs b
            result.third shouldBeSameInstanceAs c
        }
        test("converts a MutableTriple") {
            val a = Any(); val b = Any(); val c = Any()
            val result = MutableTriple.of(a, b, c).toKotlinTriple()
            result.first shouldBeSameInstanceAs a
            result.second shouldBeSameInstanceAs b
            result.third shouldBeSameInstanceAs c
        }
    }

    // ── Destructuring operators ───────────────────────────────────────────────

    context("CommonsPair destructuring") {
        test("destructures an ImmutablePair") {
            val a = Any(); val b = Any()
            val (first, second) = ImmutablePair.of(a, b)
            first shouldBeSameInstanceAs a
            second shouldBeSameInstanceAs b
        }
        test("destructures a MutablePair") {
            val a = Any(); val b = Any()
            val (first, second) = MutablePair.of(a, b)
            first shouldBeSameInstanceAs a
            second shouldBeSameInstanceAs b
        }
    }

    context("CommonsTriple destructuring") {
        test("destructures an ImmutableTriple") {
            val a = Any(); val b = Any(); val c = Any()
            val (first, second, third) = ImmutableTriple.of(a, b, c)
            first shouldBeSameInstanceAs a
            second shouldBeSameInstanceAs b
            third shouldBeSameInstanceAs c
        }
        test("destructures a MutableTriple") {
            val a = Any(); val b = Any(); val c = Any()
            val (first, second, third) = MutableTriple.of(a, b, c)
            first shouldBeSameInstanceAs a
            second shouldBeSameInstanceAs b
            third shouldBeSameInstanceAs c
        }
    }
})
