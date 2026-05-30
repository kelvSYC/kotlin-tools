package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BiMapTest : FunSpec({

    context("construction") {
        test("emptyBiMap produces an empty map") {
            val m = emptyBiMap<String, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("biMapOf with no pairs produces an empty map") {
            biMapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("biMapOf with pairs") {
            val m = biMapOf("a" to 1, "b" to 2)
            m.size shouldBe 2
            m["a"] shouldBe 1
            m["b"] shouldBe 2
        }

        test("biMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                biMapOf("a" to 1, "b" to 1)
            }
        }

        test("buildBiMap") {
            val m = buildBiMap<String, Int> {
                put("a", 1)
                put("b", 2)
            }
            m.size shouldBe 2
            m["a"] shouldBe 1
        }

        test("toBiMap from Map") {
            val m = mapOf("a" to 1, "b" to 2).toBiMap()
            m.size shouldBe 2
            m["a"] shouldBe 1
        }

        test("toBiMap rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                mapOf("a" to 1, "b" to 1).toBiMap()
            }
        }

        test("mutableBiMapOf empty") {
            mutableBiMapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("mutableBiMapOf with pairs") {
            val m = mutableBiMapOf("a" to 1)
            m.size shouldBe 1
            m["a"] shouldBe 1
        }

        test("buildMutableBiMap") {
            val m = buildMutableBiMap<String, Int> {
                put("a", 1)
                forcePut("b", 2)
            }
            m.size shouldBe 2
        }

        test("toMutableBiMap from Map") {
            val m = mapOf("a" to 1).toMutableBiMap()
            m["a"] shouldBe 1
            m["b"] = 2
            m.size shouldBe 2
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            val m = biMapOf("a" to 1)
            m["a"] shouldBe 1
        }

        test("get returns null for absent key") {
            val m = biMapOf("a" to 1)
            m["b"].shouldBeNull()
        }

        test("containsKey true for present key") {
            biMapOf("a" to 1).containsKey("a").shouldBeTrue()
        }

        test("containsKey false for absent key") {
            biMapOf("a" to 1).containsKey("b").shouldBeFalse()
        }

        test("containsValue true for present value") {
            biMapOf("a" to 1).containsValue(1).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            biMapOf("a" to 1).containsValue(2).shouldBeFalse()
        }
    }

    context("put contract") {
        test("put returns null for new key") {
            val m = mutableBiMapOf<String, Int>()
            m.put("a", 1).shouldBeNull()
        }

        test("put returns previous value for existing key") {
            val m = mutableBiMapOf("a" to 1)
            m.put("a", 2) shouldBe 1
            m["a"] shouldBe 2
        }

        test("put throws when value already exists under different key") {
            val m = mutableBiMapOf("a" to 1)
            shouldThrow<IllegalArgumentException> {
                m.put("b", 1)
            }
        }

        test("put with same key and same value is a no-op") {
            val m = mutableBiMapOf("a" to 1)
            m.put("a", 1) shouldBe 1
            m.size shouldBe 1
        }
    }

    context("forcePut") {
        test("forcePut inserts new entry") {
            val m = mutableBiMapOf<String, Int>()
            m.forcePut("a", 1).shouldBeNull()
            m["a"] shouldBe 1
        }

        test("forcePut returns previous value for existing key") {
            val m = mutableBiMapOf("a" to 1)
            m.forcePut("a", 2) shouldBe 1
            m["a"] shouldBe 2
        }

        test("forcePut displaces conflicting key") {
            val m = mutableBiMapOf("a" to 1, "b" to 2)
            m.forcePut("c", 1)
            m.containsKey("a").shouldBeFalse()
            m["c"] shouldBe 1
            m.size shouldBe 2
        }

        test("forcePut returns null when no previous value for target key") {
            val m = mutableBiMapOf("a" to 1)
            m.forcePut("b", 1).shouldBeNull()
        }

        test("forcePut same key and value is a no-op") {
            val m = mutableBiMapOf("a" to 1)
            m.forcePut("a", 1) shouldBe 1
            m.size shouldBe 1
        }
    }

    context("remove") {
        test("remove returns previous value") {
            val m = mutableBiMapOf("a" to 1)
            m.remove("a") shouldBe 1
            m.containsKey("a").shouldBeFalse()
        }

        test("remove returns null for absent key") {
            mutableBiMapOf<String, Int>().remove("a").shouldBeNull()
        }

        test("remove keeps inverse consistent") {
            val m = mutableBiMapOf("a" to 1, "b" to 2)
            m.remove("a")
            m.inverse.containsKey(1).shouldBeFalse()
            m.inverse.containsKey(2).shouldBeTrue()
        }
    }

    context("clear") {
        test("clear empties both forward and inverse") {
            val m = mutableBiMapOf("a" to 1, "b" to 2)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.inverse.isEmpty().shouldBeTrue()
        }
    }

    context("inverse") {
        test("inverse maps values to keys") {
            val m = biMapOf("a" to 1, "b" to 2)
            m.inverse[1] shouldBe "a"
            m.inverse[2] shouldBe "b"
        }

        test("inverse is a live view of mutations") {
            val m = mutableBiMapOf("a" to 1)
            m["b"] = 2
            m.inverse.containsKey(2).shouldBeTrue()
            m.inverse[2] shouldBe "b"
        }

        test("mutation through inverse is reflected in forward") {
            val m = mutableBiMapOf("a" to 1, "b" to 2)
            m.inverse.remove(1)
            m.containsKey("a").shouldBeFalse()
            m.size shouldBe 1
        }

        test("inverse.inverse has same entries as original") {
            val m = biMapOf("a" to 1, "b" to 2)
            m.inverse.inverse shouldBe m
        }

        test("inverse size matches forward size") {
            val m = mutableBiMapOf("a" to 1, "b" to 2)
            m.inverse.size shouldBe m.size
            m.remove("a")
            m.inverse.size shouldBe m.size
        }
    }

    context("views") {
        test("keys contains all keys") {
            val m = biMapOf("a" to 1, "b" to 2)
            m.keys.shouldContainExactlyInAnyOrder("a", "b")
        }

        test("values contains all values") {
            val m = biMapOf("a" to 1, "b" to 2)
            m.values.shouldContainExactlyInAnyOrder(1, 2)
        }

        test("entries contains all entries") {
            val m = biMapOf("a" to 1)
            val entry = m.entries.single()
            entry.key shouldBe "a"
            entry.value shouldBe 1
        }
    }

    context("equality") {
        test("equal to regular map with same entries") {
            val bm = biMapOf("a" to 1, "b" to 2)
            val rm = mapOf("a" to 1, "b" to 2)
            bm shouldBe rm
            rm shouldBe bm
        }

        test("not equal when different values") {
            biMapOf("a" to 1) shouldNotBe biMapOf("a" to 2)
        }

        test("hashCode consistent with Map contract") {
            val bm = biMapOf("a" to 1, "b" to 2)
            val rm = mapOf("a" to 1, "b" to 2)
            bm.hashCode() shouldBe rm.hashCode()
        }
    }

    context("immutability") {
        test("read-only factory results cannot be cast to MutableBiMap") {
            shouldThrow<ClassCastException> { emptyBiMap<String, Int>() as MutableBiMap<String, Int> }
            shouldThrow<ClassCastException> { biMapOf("a" to 1) as MutableBiMap<String, Int> }
            shouldThrow<ClassCastException> { buildBiMap<String, Int> { put("a", 1) } as MutableBiMap<String, Int> }
        }
    }
})
