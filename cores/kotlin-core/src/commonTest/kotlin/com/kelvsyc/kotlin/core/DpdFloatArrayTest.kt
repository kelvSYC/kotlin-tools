package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// DPD decimal32 encoding for small positive integers N (1–7):
//   biasedExponent = 101, normal encoding → combination = (101 shl 3) or 0 = 0x328
//   leadingDigit = 0, declet1 = 0, declet2 = N (for N ≤ 7 the DPD encoding equals the value)
//   →  bits = (0x328 shl 20) or N = 0x32800000 or N
private fun dpdPositiveInt(n: Int): DpdFloat = DpdFloat(0x32800000 or n)

class DpdFloatArrayTest : FunSpec({

    // ── Constructors ──────────────────────────────────────────────────────────

    context("size constructor") {
        test("creates array of given size") {
            DpdFloatArray(5).size shouldBe 5
        }

        test("zero-initialises all elements to positive zero") {
            val arr = DpdFloatArray(3)
            for (i in 0 until arr.size) arr[i].bits shouldBe 0
        }

        test("size zero is permitted") {
            DpdFloatArray(0).size shouldBe 0
        }
    }

    context("initialiser constructor") {
        test("applies init to each index") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            for (i in 0 until arr.size) arr[i].bits shouldBe dpdPositiveInt(i + 1).bits
        }

        test("size is correct") {
            DpdFloatArray(7) { DpdFloat.NaN }.size shouldBe 7
        }
    }

    context("IntArray constructor") {
        test("copies elements from source") {
            val src = intArrayOf(dpdPositiveInt(1).bits, dpdPositiveInt(2).bits)
            val arr = DpdFloatArray(src)
            arr[0].bits shouldBe dpdPositiveInt(1).bits
            arr[1].bits shouldBe dpdPositiveInt(2).bits
        }

        test("size matches source") {
            DpdFloatArray(intArrayOf(1, 2, 3)).size shouldBe 3
        }

        test("mutating source after construction does not affect array") {
            val src = intArrayOf(dpdPositiveInt(1).bits)
            val arr = DpdFloatArray(src)
            src[0] = 0
            arr[0].bits shouldBe dpdPositiveInt(1).bits
        }
    }

    // ── get / set ─────────────────────────────────────────────────────────────

    context("get and set") {
        test("set stores value and get retrieves it") {
            val arr = DpdFloatArray(3)
            arr[1] = dpdPositiveInt(2)
            arr[1].bits shouldBe dpdPositiveInt(2).bits
        }

        test("set stores by bit pattern, not numeric value") {
            val arr = DpdFloatArray(1)
            arr[0] = DpdFloat.NaN
            arr[0].isNaN() shouldBe true
        }

        test("distinct elements are independent") {
            val arr = DpdFloatArray(2)
            arr[0] = dpdPositiveInt(1)
            arr[1] = dpdPositiveInt(2)
            arr[0].bits shouldBe dpdPositiveInt(1).bits
            arr[1].bits shouldBe dpdPositiveInt(2).bits
        }
    }

    // ── indices / lastIndex / isEmpty / isNotEmpty ────────────────────────────

    context("indices and lastIndex") {
        test("indices spans 0 until size") {
            DpdFloatArray(4).indices shouldBe (0 until 4)
        }

        test("lastIndex is size minus one") {
            DpdFloatArray(4).lastIndex shouldBe 3
        }
    }

    context("isEmpty and isNotEmpty") {
        test("empty array isEmpty") {
            DpdFloatArray(0).isEmpty() shouldBe true
            DpdFloatArray(0).isNotEmpty() shouldBe false
        }

        test("non-empty array isNotEmpty") {
            DpdFloatArray(1).isEmpty() shouldBe false
            DpdFloatArray(1).isNotEmpty() shouldBe true
        }
    }

    // ── contains / indexOf / lastIndexOf ─────────────────────────────────────

    context("contains") {
        test("returns true when element is present") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            arr.contains(dpdPositiveInt(2)) shouldBe true
        }

        test("returns false when element is absent") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            arr.contains(dpdPositiveInt(7)) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val arr = DpdFloatArray(1)
            arr[0] = DpdFloat.NaN
            // NaN with payload 1: same quiet-NaN combination, continuation = 1
            arr.contains(DpdFloat(0x7E000001)) shouldBe false
        }

        test("uses bit equality: +0 and -0 are distinct") {
            val arr = DpdFloatArray(1)
            arr[0] = DpdFloat.positiveZero
            arr.contains(DpdFloat.negativeZero) shouldBe false
        }
    }

    context("indexOf") {
        test("returns index of first matching element") {
            val arr = DpdFloatArray(4) { dpdPositiveInt(it + 1) }
            arr[2] = dpdPositiveInt(1)
            arr.indexOf(dpdPositiveInt(1)) shouldBe 0
        }

        test("returns -1 when absent") {
            DpdFloatArray(3) { dpdPositiveInt(it + 1) }.indexOf(dpdPositiveInt(7)) shouldBe -1
        }
    }

    context("lastIndexOf") {
        test("returns index of last matching element") {
            val arr = DpdFloatArray(4) { dpdPositiveInt(it + 1) }
            arr[3] = dpdPositiveInt(1)
            arr.lastIndexOf(dpdPositiveInt(1)) shouldBe 3
        }

        test("returns -1 when absent") {
            DpdFloatArray(3) { dpdPositiveInt(it + 1) }.lastIndexOf(dpdPositiveInt(7)) shouldBe -1
        }
    }

    // ── iterator ──────────────────────────────────────────────────────────────

    context("iterator") {
        test("visits all elements in order") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            val collected = mutableListOf<Int>()
            val it = arr.iterator()
            while (it.hasNext()) collected.add(it.nextDpdFloat().bits)
            collected shouldBe listOf(dpdPositiveInt(1).bits, dpdPositiveInt(2).bits, dpdPositiveInt(3).bits)
        }

        test("next() and nextDpdFloat() return the same value") {
            val arr = DpdFloatArray(1)
            arr[0] = dpdPositiveInt(3)
            val it = arr.iterator()
            it.nextDpdFloat().bits shouldBe arr[0].bits
        }

        test("hasNext is false after last element") {
            val it = DpdFloatArray(1).iterator()
            it.next()
            it.hasNext() shouldBe false
        }

        test("exhausted iterator throws NoSuchElementException") {
            val it = DpdFloatArray(0).iterator()
            shouldThrow<NoSuchElementException> { it.next() }
        }

        test("empty array iterator has no elements") {
            DpdFloatArray(0).iterator().hasNext() shouldBe false
        }
    }

    // ── fill ──────────────────────────────────────────────────────────────────

    context("fill") {
        test("fills entire array by default") {
            val arr = DpdFloatArray(3)
            arr.fill(dpdPositiveInt(1))
            for (i in 0 until arr.size) arr[i].bits shouldBe dpdPositiveInt(1).bits
        }

        test("fills only the specified range") {
            val arr = DpdFloatArray(4)
            arr.fill(dpdPositiveInt(1), fromIndex = 1, toIndex = 3)
            arr[0].bits shouldBe 0
            arr[1].bits shouldBe dpdPositiveInt(1).bits
            arr[2].bits shouldBe dpdPositiveInt(1).bits
            arr[3].bits shouldBe 0
        }
    }

    // ── copyOf / copyOfRange ──────────────────────────────────────────────────

    context("copyOf") {
        test("full copy has same size and contents") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            val copy = arr.copyOf()
            copy.size shouldBe 3
            copy.contentEquals(arr) shouldBe true
        }

        test("mutating copy does not affect original") {
            val arr = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val copy = arr.copyOf()
            copy[0] = dpdPositiveInt(7)
            arr[0].bits shouldBe dpdPositiveInt(1).bits
        }

        test("copyOf with larger size pads with positive zero") {
            val arr = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val copy = arr.copyOf(4)
            copy.size shouldBe 4
            copy[2].bits shouldBe 0
            copy[3].bits shouldBe 0
        }

        test("copyOf with smaller size truncates") {
            val arr = DpdFloatArray(4) { dpdPositiveInt(it + 1) }
            val copy = arr.copyOf(2)
            copy.size shouldBe 2
            copy[0].bits shouldBe dpdPositiveInt(1).bits
            copy[1].bits shouldBe dpdPositiveInt(2).bits
        }
    }

    context("copyOfRange") {
        test("copies the specified range") {
            val arr = DpdFloatArray(4) { dpdPositiveInt(it + 1) }
            val copy = arr.copyOfRange(1, 3)
            copy.size shouldBe 2
            copy[0].bits shouldBe dpdPositiveInt(2).bits
            copy[1].bits shouldBe dpdPositiveInt(3).bits
        }
    }

    // ── copyInto ─────────────────────────────────────────────────────────────

    context("copyInto") {
        test("copies all elements into destination at offset") {
            val src = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val dst = DpdFloatArray(4)
            src.copyInto(dst, destinationOffset = 1)
            dst[0].bits shouldBe 0
            dst[1].bits shouldBe dpdPositiveInt(1).bits
            dst[2].bits shouldBe dpdPositiveInt(2).bits
            dst[3].bits shouldBe 0
        }

        test("returns the destination array") {
            val src = DpdFloatArray(1)
            val dst = DpdFloatArray(1)
            src.copyInto(dst) shouldBe dst
        }
    }

    // ── toIntArray / IntArray.toDpdFloatArray ─────────────────────────────────

    context("toIntArray") {
        test("returned array has same bit patterns") {
            val arr = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val ints = arr.toIntArray()
            ints[0] shouldBe arr[0].bits
            ints[1] shouldBe arr[1].bits
        }

        test("mutating returned array does not affect original") {
            val arr = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val ints = arr.toIntArray()
            ints[0] = 0
            arr[0].bits shouldNotBe 0
        }
    }

    context("IntArray.toDpdFloatArray") {
        test("produces array with same bit patterns") {
            val ints = intArrayOf(dpdPositiveInt(1).bits, dpdPositiveInt(2).bits)
            val arr = ints.toDpdFloatArray()
            arr[0].bits shouldBe dpdPositiveInt(1).bits
            arr[1].bits shouldBe dpdPositiveInt(2).bits
        }

        test("mutating source after conversion does not affect result") {
            val ints = intArrayOf(dpdPositiveInt(1).bits)
            val arr = ints.toDpdFloatArray()
            ints[0] = 0
            arr[0].bits shouldBe dpdPositiveInt(1).bits
        }
    }

    // ── sort ──────────────────────────────────────────────────────────────────

    context("sort") {
        test("sorts positive values in ascending numeric order") {
            val arr = DpdFloatArray(4)
            arr[0] = dpdPositiveInt(3); arr[1] = dpdPositiveInt(1)
            arr[2] = dpdPositiveInt(2); arr[3] = DpdFloat.positiveZero
            arr.sort()
            arr[0].bits shouldBe DpdFloat.positiveZero.bits
            arr[1].bits shouldBe dpdPositiveInt(1).bits
            arr[2].bits shouldBe dpdPositiveInt(2).bits
            arr[3].bits shouldBe dpdPositiveInt(3).bits
        }

        test("places negative values before positive values") {
            val neg1 = DpdFloat(Int.MIN_VALUE or dpdPositiveInt(1).bits)
            val arr = DpdFloatArray(2)
            arr[0] = dpdPositiveInt(1); arr[1] = neg1
            arr.sort()
            arr[0].bits shouldBe neg1.bits
            arr[1].bits shouldBe dpdPositiveInt(1).bits
        }

        test("sort with range only reorders the specified slice") {
            val arr = DpdFloatArray(4)
            arr[0] = dpdPositiveInt(7)
            arr[1] = dpdPositiveInt(3); arr[2] = dpdPositiveInt(1)
            arr[3] = dpdPositiveInt(5)
            arr.sort(fromIndex = 1, toIndex = 3)
            arr[0].bits shouldBe dpdPositiveInt(7).bits
            arr[1].bits shouldBe dpdPositiveInt(1).bits
            arr[2].bits shouldBe dpdPositiveInt(3).bits
            arr[3].bits shouldBe dpdPositiveInt(5).bits
        }
    }

    // ── contentEquals / contentHashCode / contentToString ────────────────────

    context("contentEquals") {
        test("equal arrays are content-equal") {
            val a = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val b = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            a.contentEquals(b) shouldBe true
        }

        test("arrays with different values are not content-equal") {
            val a = DpdFloatArray(1); a[0] = dpdPositiveInt(1)
            val b = DpdFloatArray(1); b[0] = dpdPositiveInt(2)
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val a = DpdFloatArray(1); a[0] = DpdFloat.NaN
            val b = DpdFloatArray(1); b[0] = DpdFloat(0x7E000001)
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: +0 and -0 are not equal") {
            val a = DpdFloatArray(1); a[0] = DpdFloat.positiveZero
            val b = DpdFloatArray(1); b[0] = DpdFloat.negativeZero
            a.contentEquals(b) shouldBe false
        }
    }

    context("contentHashCode") {
        test("equal arrays have equal content hash codes") {
            val a = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            val b = DpdFloatArray(2) { dpdPositiveInt(it + 1) }
            a.contentHashCode() shouldBe b.contentHashCode()
        }
    }

    context("contentToString") {
        test("empty array produces empty brackets") {
            DpdFloatArray(0).contentToString() shouldBe "[]"
        }

        test("single element") {
            val v = dpdPositiveInt(1)
            val arr = DpdFloatArray(1); arr[0] = v
            arr.contentToString() shouldBe "[$v]"
        }

        test("multiple elements are comma-separated") {
            val v0 = DpdFloat.positiveZero
            val v1 = dpdPositiveInt(1)
            val arr = DpdFloatArray(2); arr[0] = v0; arr[1] = v1
            arr.contentToString() shouldBe "[$v0, $v1]"
        }
    }

    // ── asList ────────────────────────────────────────────────────────────────

    context("asList") {
        test("size matches array") {
            DpdFloatArray(3).asList().size shouldBe 3
        }

        test("element access matches array") {
            val arr = DpdFloatArray(3) { dpdPositiveInt(it + 1) }
            val list = arr.asList()
            for (i in 0 until arr.size) list[i].bits shouldBe arr[i].bits
        }

        test("is a live view: mutation of array is reflected in list") {
            val arr = DpdFloatArray(2)
            val list = arr.asList()
            arr[0] = dpdPositiveInt(5)
            list[0].bits shouldBe dpdPositiveInt(5).bits
        }
    }

    // ── destructuring ─────────────────────────────────────────────────────────

    context("destructuring") {
        test("components 1 through 5 correspond to indices 0 through 4") {
            val arr = DpdFloatArray(5) { dpdPositiveInt(it + 1) }
            val (a, b, c, d, e) = arr
            a.bits shouldBe dpdPositiveInt(1).bits
            b.bits shouldBe dpdPositiveInt(2).bits
            c.bits shouldBe dpdPositiveInt(3).bits
            d.bits shouldBe dpdPositiveInt(4).bits
            e.bits shouldBe dpdPositiveInt(5).bits
        }
    }
})
