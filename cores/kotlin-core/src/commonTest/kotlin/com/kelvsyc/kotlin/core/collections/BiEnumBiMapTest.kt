package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.enums.enumEntries

private enum class Rank { ACE, TWO, THREE }
private enum class Shape { CIRCLE, SQUARE, TRIANGLE }

class BiEnumBiMapTest : FunSpec({

    context("construction") {
        test("biEnumBiMapOf with no pairs produces an empty map") {
            biEnumBiMapOf<Rank, Shape>().isEmpty().shouldBeTrue()
        }

        test("biEnumBiMapOf exposes enumEntries and valueEnumEntries") {
            val m = biEnumBiMapOf<Rank, Shape>()
            m.enumEntries shouldBe enumEntries<Rank>()
            m.valueEnumEntries shouldBe enumEntries<Shape>()
        }

        test("biEnumBiMapOf with pairs") {
            val m = biEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.size shouldBe 2
            m[Rank.ACE] shouldBe Shape.CIRCLE
            m[Rank.TWO] shouldBe Shape.SQUARE
        }

        test("biEnumBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                biEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.CIRCLE)
            }
        }

        test("buildBiEnumBiMap") {
            val m = buildBiEnumBiMap<Rank, Shape> {
                put(Rank.ACE, Shape.CIRCLE)
                put(Rank.TWO, Shape.SQUARE)
            }
            m.size shouldBe 2
            m[Rank.ACE] shouldBe Shape.CIRCLE
        }

        test("toMutableBiEnumBiMap from Map") {
            val m = mapOf(Rank.ACE to Shape.CIRCLE).toMutableBiEnumBiMap()
            m[Rank.TWO] = Shape.SQUARE
            m.size shouldBe 2
        }

        test("mutableBiEnumBiMapOf with pairs") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            m.size shouldBe 1
            m[Rank.ACE] shouldBe Shape.CIRCLE
        }
    }

    context("enumEntries") {
        test("enumEntries and valueEnumEntries are correct") {
            val m = mutableBiEnumBiMapOf<Rank, Shape>()
            m.enumEntries shouldBe enumEntries<Rank>()
            m.valueEnumEntries shouldBe enumEntries<Shape>()
        }
    }

    context("ordinal iteration order on keys") {
        test("keys iterate in ordinal order regardless of insertion order") {
            val m = mutableBiEnumBiMapOf(Rank.THREE to Shape.TRIANGLE, Rank.ACE to Shape.CIRCLE)
            m.keys.toList().shouldContainExactlyInAnyOrder(Rank.ACE, Rank.THREE)
            // Exact ordinal order
            m.keys.toList() shouldBe listOf(Rank.ACE, Rank.THREE)
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE)[Rank.ACE] shouldBe Shape.CIRCLE
        }

        test("get returns null for absent key") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE)[Rank.TWO].shouldBeNull()
        }

        test("containsKey true for present key") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE).containsKey(Rank.ACE).shouldBeTrue()
        }

        test("containsKey false for absent key") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE).containsKey(Rank.TWO).shouldBeFalse()
        }

        test("containsValue true for present value") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE).containsValue(Shape.CIRCLE).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE).containsValue(Shape.SQUARE).shouldBeFalse()
        }
    }

    context("put contract") {
        test("put throws when value already exists under different key") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            shouldThrow<IllegalArgumentException> { m.put(Rank.TWO, Shape.CIRCLE) }
        }

        test("put with same key and value is a no-op") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            m.put(Rank.ACE, Shape.CIRCLE) shouldBe Shape.CIRCLE
            m.size shouldBe 1
        }
    }

    context("forcePut") {
        test("forcePut displaces conflicting key") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.forcePut(Rank.THREE, Shape.CIRCLE)
            m.containsKey(Rank.ACE).shouldBeFalse()
            m[Rank.THREE] shouldBe Shape.CIRCLE
            m.size shouldBe 2
        }
    }

    context("remove") {
        test("remove keeps inverse consistent") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.remove(Rank.ACE)
            m.inverse.containsKey(Shape.CIRCLE).shouldBeFalse()
            m.inverse.containsKey(Shape.SQUARE).shouldBeTrue()
        }
    }

    context("inverse") {
        test("inverse maps values to keys and is an EnumBiMap on value entries") {
            val m = biEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.inverse[Shape.CIRCLE] shouldBe Rank.ACE
            // inverse is MutableEnumBiMap<Shape, Rank>
            m.inverse.enumEntries shouldBe enumEntries<Shape>()
        }

        test("inverse is a live view") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            m[Rank.TWO] = Shape.SQUARE
            m.inverse.containsKey(Shape.SQUARE).shouldBeTrue()
        }

        test("mutation through inverse reflected in forward") {
            val m = mutableBiEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.inverse.remove(Shape.CIRCLE)
            m.containsKey(Rank.ACE).shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("equality") {
        test("equal to regular map with same entries") {
            val bm = biEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            val rm = mapOf(Rank.ACE to Shape.CIRCLE)
            bm shouldBe rm
            rm shouldBe bm
        }

        test("not equal when different values") {
            biEnumBiMapOf(Rank.ACE to Shape.CIRCLE) shouldNotBe biEnumBiMapOf(Rank.ACE to Shape.SQUARE)
        }

        test("hashCode consistent with Map contract") {
            val bm = biEnumBiMapOf(Rank.ACE to Shape.CIRCLE)
            val rm = mapOf(Rank.ACE to Shape.CIRCLE)
            bm.hashCode() shouldBe rm.hashCode()
        }
    }

    context("values") {
        test("values contains all values") {
            val m = biEnumBiMapOf(Rank.ACE to Shape.CIRCLE, Rank.TWO to Shape.SQUARE)
            m.values.shouldContainExactlyInAnyOrder(Shape.CIRCLE, Shape.SQUARE)
        }
    }
})
