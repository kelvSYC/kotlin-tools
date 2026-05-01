package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

// BID decimal32 encoding for small positive integers N (1–7):
//   biasedExponent = 101, normal encoding → combination = (101 shl 3) or 0 = 0x328
//   significand = N (fits in continuation field)  →  bits = (0x328 shl 20) or N = 0x32800000 or N
private fun bidPositiveInt(n: Int): BidFloat = BidFloat(0x32800000 or n)

class BidFloatArrayTest : FunSpec({

    // ── Constructors ──────────────────────────────────────────────────────────

    context("size constructor") {
        test("creates array of given size") {
            BidFloatArray(5).size shouldBe 5
        }

        test("zero-initialises all elements to positive zero") {
            val arr = BidFloatArray(3)
            for (i in 0 until arr.size) arr[i].bits shouldBe 0
        }

        test("size zero is permitted") {
            BidFloatArray(0).size shouldBe 0
        }
    }

    context("initialiser constructor") {
        test("applies init to each index") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            for (i in 0 until arr.size) arr[i].bits shouldBe bidPositiveInt(i + 1).bits
        }

        test("size is correct") {
            BidFloatArray(7) { BidFloat.NaN }.size shouldBe 7
        }
    }

    context("IntArray constructor") {
        test("copies elements from source") {
            val src = intArrayOf(bidPositiveInt(1).bits, bidPositiveInt(2).bits)
            val arr = BidFloatArray(src)
            arr[0].bits shouldBe bidPositiveInt(1).bits
            arr[1].bits shouldBe bidPositiveInt(2).bits
        }

        test("size matches source") {
            BidFloatArray(intArrayOf(1, 2, 3)).size shouldBe 3
        }

        test("mutating source after construction does not affect array") {
            val src = intArrayOf(bidPositiveInt(1).bits)
            val arr = BidFloatArray(src)
            src[0] = 0
            arr[0].bits shouldBe bidPositiveInt(1).bits
        }
    }

    // ── get / set ─────────────────────────────────────────────────────────────

    context("get and set") {
        test("set stores value and get retrieves it") {
            val arr = BidFloatArray(3)
            arr[1] = bidPositiveInt(2)
            arr[1].bits shouldBe bidPositiveInt(2).bits
        }

        test("set stores by bit pattern, not numeric value") {
            val arr = BidFloatArray(1)
            arr[0] = BidFloat.NaN
            arr[0].isNaN() shouldBe true
        }

        test("distinct elements are independent") {
            val arr = BidFloatArray(2)
            arr[0] = bidPositiveInt(1)
            arr[1] = bidPositiveInt(2)
            arr[0].bits shouldBe bidPositiveInt(1).bits
            arr[1].bits shouldBe bidPositiveInt(2).bits
        }
    }

    // ── indices / lastIndex / isEmpty / isNotEmpty ────────────────────────────

    context("indices and lastIndex") {
        test("indices spans 0 until size") {
            BidFloatArray(4).indices shouldBe (0 until 4)
        }

        test("lastIndex is size minus one") {
            BidFloatArray(4).lastIndex shouldBe 3
        }
    }

    context("isEmpty and isNotEmpty") {
        test("empty array isEmpty") {
            BidFloatArray(0).isEmpty() shouldBe true
            BidFloatArray(0).isNotEmpty() shouldBe false
        }

        test("non-empty array isNotEmpty") {
            BidFloatArray(1).isEmpty() shouldBe false
            BidFloatArray(1).isNotEmpty() shouldBe true
        }
    }

    // ── contains / indexOf / lastIndexOf ─────────────────────────────────────

    context("contains") {
        test("returns true when element is present") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            arr.contains(bidPositiveInt(2)) shouldBe true
        }

        test("returns false when element is absent") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            arr.contains(bidPositiveInt(7)) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val arr = BidFloatArray(1)
            arr[0] = BidFloat.NaN
            // NaN with payload 1: same quiet-NaN combination, continuation = 1
            arr.contains(BidFloat(0x7E000001)) shouldBe false
        }

        test("uses bit equality: +0 and -0 are distinct") {
            val arr = BidFloatArray(1)
            arr[0] = BidFloat.positiveZero
            arr.contains(BidFloat.negativeZero) shouldBe false
        }
    }

    context("indexOf") {
        test("returns index of first matching element") {
            val arr = BidFloatArray(4) { bidPositiveInt(it + 1) }
            arr[2] = bidPositiveInt(1)
            arr.indexOf(bidPositiveInt(1)) shouldBe 0
        }

        test("returns -1 when absent") {
            BidFloatArray(3) { bidPositiveInt(it + 1) }.indexOf(bidPositiveInt(7)) shouldBe -1
        }
    }

    context("lastIndexOf") {
        test("returns index of last matching element") {
            val arr = BidFloatArray(4) { bidPositiveInt(it + 1) }
            arr[3] = bidPositiveInt(1)
            arr.lastIndexOf(bidPositiveInt(1)) shouldBe 3
        }

        test("returns -1 when absent") {
            BidFloatArray(3) { bidPositiveInt(it + 1) }.lastIndexOf(bidPositiveInt(7)) shouldBe -1
        }
    }

    // ── iterator ──────────────────────────────────────────────────────────────

    context("iterator") {
        test("visits all elements in order") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            val collected = mutableListOf<Int>()
            val it = arr.iterator()
            while (it.hasNext()) collected.add(it.nextBidFloat().bits)
            collected shouldBe listOf(bidPositiveInt(1).bits, bidPositiveInt(2).bits, bidPositiveInt(3).bits)
        }

        test("next() and nextBidFloat() return the same value") {
            val arr = BidFloatArray(1)
            arr[0] = bidPositiveInt(3)
            val it = arr.iterator()
            it.nextBidFloat().bits shouldBe arr[0].bits
        }

        test("hasNext is false after last element") {
            val it = BidFloatArray(1).iterator()
            it.next()
            it.hasNext() shouldBe false
        }

        test("exhausted iterator throws NoSuchElementException") {
            val it = BidFloatArray(0).iterator()
            shouldThrow<NoSuchElementException> { it.next() }
        }

        test("empty array iterator has no elements") {
            BidFloatArray(0).iterator().hasNext() shouldBe false
        }
    }

    // ── fill ──────────────────────────────────────────────────────────────────

    context("fill") {
        test("fills entire array by default") {
            val arr = BidFloatArray(3)
            arr.fill(bidPositiveInt(1))
            for (i in 0 until arr.size) arr[i].bits shouldBe bidPositiveInt(1).bits
        }

        test("fills only the specified range") {
            val arr = BidFloatArray(4)
            arr.fill(bidPositiveInt(1), fromIndex = 1, toIndex = 3)
            arr[0].bits shouldBe 0
            arr[1].bits shouldBe bidPositiveInt(1).bits
            arr[2].bits shouldBe bidPositiveInt(1).bits
            arr[3].bits shouldBe 0
        }
    }

    // ── copyOf / copyOfRange ──────────────────────────────────────────────────

    context("copyOf") {
        test("full copy has same size and contents") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            val copy = arr.copyOf()
            copy.size shouldBe 3
            copy.contentEquals(arr) shouldBe true
        }

        test("mutating copy does not affect original") {
            val arr = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val copy = arr.copyOf()
            copy[0] = bidPositiveInt(7)
            arr[0].bits shouldBe bidPositiveInt(1).bits
        }

        test("copyOf with larger size pads with positive zero") {
            val arr = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val copy = arr.copyOf(4)
            copy.size shouldBe 4
            copy[2].bits shouldBe 0
            copy[3].bits shouldBe 0
        }

        test("copyOf with smaller size truncates") {
            val arr = BidFloatArray(4) { bidPositiveInt(it + 1) }
            val copy = arr.copyOf(2)
            copy.size shouldBe 2
            copy[0].bits shouldBe bidPositiveInt(1).bits
            copy[1].bits shouldBe bidPositiveInt(2).bits
        }
    }

    context("copyOfRange") {
        test("copies the specified range") {
            val arr = BidFloatArray(4) { bidPositiveInt(it + 1) }
            val copy = arr.copyOfRange(1, 3)
            copy.size shouldBe 2
            copy[0].bits shouldBe bidPositiveInt(2).bits
            copy[1].bits shouldBe bidPositiveInt(3).bits
        }
    }

    // ── copyInto ─────────────────────────────────────────────────────────────

    context("copyInto") {
        test("copies all elements into destination at offset") {
            val src = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val dst = BidFloatArray(4)
            src.copyInto(dst, destinationOffset = 1)
            dst[0].bits shouldBe 0
            dst[1].bits shouldBe bidPositiveInt(1).bits
            dst[2].bits shouldBe bidPositiveInt(2).bits
            dst[3].bits shouldBe 0
        }

        test("returns the destination array") {
            val src = BidFloatArray(1)
            val dst = BidFloatArray(1)
            src.copyInto(dst) shouldBe dst
        }
    }

    // ── toIntArray / IntArray.toBidFloatArray ─────────────────────────────────

    context("toIntArray") {
        test("returned array has same bit patterns") {
            val arr = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val ints = arr.toIntArray()
            ints[0] shouldBe arr[0].bits
            ints[1] shouldBe arr[1].bits
        }

        test("mutating returned array does not affect original") {
            val arr = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val ints = arr.toIntArray()
            ints[0] = 0
            arr[0].bits shouldNotBe 0
        }
    }

    context("IntArray.toBidFloatArray") {
        test("produces array with same bit patterns") {
            val ints = intArrayOf(bidPositiveInt(1).bits, bidPositiveInt(2).bits)
            val arr = ints.toBidFloatArray()
            arr[0].bits shouldBe bidPositiveInt(1).bits
            arr[1].bits shouldBe bidPositiveInt(2).bits
        }

        test("mutating source after conversion does not affect result") {
            val ints = intArrayOf(bidPositiveInt(1).bits)
            val arr = ints.toBidFloatArray()
            ints[0] = 0
            arr[0].bits shouldBe bidPositiveInt(1).bits
        }
    }

    // ── sort ──────────────────────────────────────────────────────────────────

    context("sort") {
        test("sorts positive values in ascending numeric order") {
            val arr = BidFloatArray(4)
            arr[0] = bidPositiveInt(3); arr[1] = bidPositiveInt(1)
            arr[2] = bidPositiveInt(2); arr[3] = BidFloat.positiveZero
            arr.sort()
            arr[0].bits shouldBe BidFloat.positiveZero.bits
            arr[1].bits shouldBe bidPositiveInt(1).bits
            arr[2].bits shouldBe bidPositiveInt(2).bits
            arr[3].bits shouldBe bidPositiveInt(3).bits
        }

        test("places negative values before positive values") {
            val neg1 = BidFloat(Int.MIN_VALUE or bidPositiveInt(1).bits)
            val arr = BidFloatArray(2)
            arr[0] = bidPositiveInt(1); arr[1] = neg1
            arr.sort()
            arr[0].bits shouldBe neg1.bits
            arr[1].bits shouldBe bidPositiveInt(1).bits
        }

        test("sort with range only reorders the specified slice") {
            val arr = BidFloatArray(4)
            arr[0] = bidPositiveInt(7)
            arr[1] = bidPositiveInt(3); arr[2] = bidPositiveInt(1)
            arr[3] = bidPositiveInt(5)
            arr.sort(fromIndex = 1, toIndex = 3)
            arr[0].bits shouldBe bidPositiveInt(7).bits
            arr[1].bits shouldBe bidPositiveInt(1).bits
            arr[2].bits shouldBe bidPositiveInt(3).bits
            arr[3].bits shouldBe bidPositiveInt(5).bits
        }
    }

    // ── contentEquals / contentHashCode / contentToString ────────────────────

    context("contentEquals") {
        test("equal arrays are content-equal") {
            val a = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val b = BidFloatArray(2) { bidPositiveInt(it + 1) }
            a.contentEquals(b) shouldBe true
        }

        test("arrays with different values are not content-equal") {
            val a = BidFloatArray(1); a[0] = bidPositiveInt(1)
            val b = BidFloatArray(1); b[0] = bidPositiveInt(2)
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val a = BidFloatArray(1); a[0] = BidFloat.NaN
            val b = BidFloatArray(1); b[0] = BidFloat(0x7E000001)
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: +0 and -0 are not equal") {
            val a = BidFloatArray(1); a[0] = BidFloat.positiveZero
            val b = BidFloatArray(1); b[0] = BidFloat.negativeZero
            a.contentEquals(b) shouldBe false
        }
    }

    context("contentHashCode") {
        test("equal arrays have equal content hash codes") {
            val a = BidFloatArray(2) { bidPositiveInt(it + 1) }
            val b = BidFloatArray(2) { bidPositiveInt(it + 1) }
            a.contentHashCode() shouldBe b.contentHashCode()
        }
    }

    context("contentToString") {
        test("empty array produces empty brackets") {
            BidFloatArray(0).contentToString() shouldBe "[]"
        }

        test("single element") {
            val v = bidPositiveInt(1)
            val arr = BidFloatArray(1); arr[0] = v
            arr.contentToString() shouldBe "[$v]"
        }

        test("multiple elements are comma-separated") {
            val v0 = BidFloat.positiveZero
            val v1 = bidPositiveInt(1)
            val arr = BidFloatArray(2); arr[0] = v0; arr[1] = v1
            arr.contentToString() shouldBe "[$v0, $v1]"
        }
    }

    // ── asList ────────────────────────────────────────────────────────────────

    context("asList") {
        test("size matches array") {
            BidFloatArray(3).asList().size shouldBe 3
        }

        test("element access matches array") {
            val arr = BidFloatArray(3) { bidPositiveInt(it + 1) }
            val list = arr.asList()
            for (i in 0 until arr.size) list[i].bits shouldBe arr[i].bits
        }

        test("is a live view: mutation of array is reflected in list") {
            val arr = BidFloatArray(2)
            val list = arr.asList()
            arr[0] = bidPositiveInt(5)
            list[0].bits shouldBe bidPositiveInt(5).bits
        }
    }

    // ── destructuring ─────────────────────────────────────────────────────────

    context("destructuring") {
        test("components 1 through 5 correspond to indices 0 through 4") {
            val arr = BidFloatArray(5) { bidPositiveInt(it + 1) }
            val (a, b, c, d, e) = arr
            a.bits shouldBe bidPositiveInt(1).bits
            b.bits shouldBe bidPositiveInt(2).bits
            c.bits shouldBe bidPositiveInt(3).bits
            d.bits shouldBe bidPositiveInt(4).bits
            e.bits shouldBe bidPositiveInt(5).bits
        }
    }
})
