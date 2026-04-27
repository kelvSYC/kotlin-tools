package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class Float16ArrayTest : FunSpec({

    // ── Constructors ──────────────────────────────────────────────────────────

    context("size constructor") {
        test("creates array of given size") {
            Float16Array(5).size shouldBe 5
        }

        test("zero-initialises all elements to positive zero") {
            val arr = Float16Array(3)
            for (i in 0 until arr.size) arr[i].bits shouldBe 0.toShort()
        }

        test("size zero is permitted") {
            Float16Array(0).size shouldBe 0
        }
    }

    context("initialiser constructor") {
        test("applies init to each index") {
            val arr = Float16Array(4) { Float16(it.toFloat()) }
            for (i in 0 until arr.size) arr[i].toFloat() shouldBe i.toFloat()
        }

        test("size is correct") {
            Float16Array(7) { Float16.NaN }.size shouldBe 7
        }
    }

    context("ShortArray constructor") {
        test("copies elements from source") {
            val src = shortArrayOf(0x3C00.toShort(), 0x4000.toShort())
            val arr = Float16Array(src)
            arr[0].bits shouldBe 0x3C00.toShort()
            arr[1].bits shouldBe 0x4000.toShort()
        }

        test("size matches source") {
            Float16Array(shortArrayOf(1, 2, 3)).size shouldBe 3
        }

        test("mutating source after construction does not affect array") {
            val src = shortArrayOf(0x3C00.toShort())
            val arr = Float16Array(src)
            src[0] = 0.toShort()
            arr[0].bits shouldBe 0x3C00.toShort()
        }
    }

    // ── get / set ─────────────────────────────────────────────────────────────

    context("get and set") {
        test("set stores value and get retrieves it") {
            val arr = Float16Array(3)
            arr[1] = Float16(2.0f)
            arr[1].toFloat() shouldBe 2.0f
        }

        test("set stores by bit pattern, not numeric value") {
            val arr = Float16Array(1)
            arr[0] = Float16.NaN
            arr[0].isNaN() shouldBe true
        }

        test("distinct elements are independent") {
            val arr = Float16Array(2)
            arr[0] = Float16(1.0f)
            arr[1] = Float16(2.0f)
            arr[0].toFloat() shouldBe 1.0f
            arr[1].toFloat() shouldBe 2.0f
        }
    }

    // ── indices / lastIndex / isEmpty / isNotEmpty ────────────────────────────

    context("indices and lastIndex") {
        test("indices spans 0 until size") {
            Float16Array(4).indices shouldBe (0 until 4)
        }

        test("lastIndex is size minus one") {
            Float16Array(4).lastIndex shouldBe 3
        }
    }

    context("isEmpty and isNotEmpty") {
        test("empty array isEmpty") {
            Float16Array(0).isEmpty() shouldBe true
            Float16Array(0).isNotEmpty() shouldBe false
        }

        test("non-empty array isNotEmpty") {
            Float16Array(1).isEmpty() shouldBe false
            Float16Array(1).isNotEmpty() shouldBe true
        }
    }

    // ── contains / indexOf / lastIndexOf ─────────────────────────────────────

    context("contains") {
        test("returns true when element is present") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            arr.contains(Float16(1.0f)) shouldBe true
        }

        test("returns false when element is absent") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            arr.contains(Float16(99.0f)) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val arr = Float16Array(1)
            arr[0] = Float16.NaN
            arr.contains(Float16(0x7C01.toShort())) shouldBe false
        }

        test("uses bit equality: +0 and -0 are distinct") {
            val arr = Float16Array(1)
            arr[0] = Float16(0)
            arr.contains(-Float16(0)) shouldBe false
        }
    }

    context("indexOf") {
        test("returns index of first matching element") {
            val arr = Float16Array(4) { Float16(it.toFloat()) }
            arr[2] = Float16(1.0f)
            arr.indexOf(Float16(1.0f)) shouldBe 1
        }

        test("returns -1 when absent") {
            Float16Array(3) { Float16(it.toFloat()) }.indexOf(Float16(99.0f)) shouldBe -1
        }
    }

    context("lastIndexOf") {
        test("returns index of last matching element") {
            val arr = Float16Array(4) { Float16(it.toFloat()) }
            arr[3] = Float16(0.0f)
            arr.lastIndexOf(Float16(0.0f)) shouldBe 3
        }

        test("returns -1 when absent") {
            Float16Array(3) { Float16(it.toFloat()) }.lastIndexOf(Float16(99.0f)) shouldBe -1
        }
    }

    // ── iterator ──────────────────────────────────────────────────────────────

    context("iterator") {
        test("visits all elements in order") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            val collected = mutableListOf<Float>()
            val it = arr.iterator()
            while (it.hasNext()) collected.add(it.nextFloat16().toFloat())
            collected shouldBe listOf(0.0f, 1.0f, 2.0f)
        }

        test("next() and nextFloat16() return the same value") {
            val arr = Float16Array(1)
            arr[0] = Float16(3.0f)
            val it = arr.iterator()
            it.nextFloat16().bits shouldBe arr[0].bits
        }

        test("hasNext is false after last element") {
            val it = Float16Array(1).iterator()
            it.next()
            it.hasNext() shouldBe false
        }

        test("exhausted iterator throws NoSuchElementException") {
            val it = Float16Array(0).iterator()
            shouldThrow<NoSuchElementException> { it.next() }
        }

        test("empty array iterator has no elements") {
            Float16Array(0).iterator().hasNext() shouldBe false
        }
    }

    // ── fill ──────────────────────────────────────────────────────────────────

    context("fill") {
        test("fills entire array by default") {
            val arr = Float16Array(3)
            arr.fill(Float16(1.0f))
            for (i in 0 until arr.size) arr[i].toFloat() shouldBe 1.0f
        }

        test("fills only the specified range") {
            val arr = Float16Array(4)
            arr.fill(Float16(1.0f), fromIndex = 1, toIndex = 3)
            arr[0].bits shouldBe 0.toShort()
            arr[1].toFloat() shouldBe 1.0f
            arr[2].toFloat() shouldBe 1.0f
            arr[3].bits shouldBe 0.toShort()
        }
    }

    // ── copyOf / copyOfRange ──────────────────────────────────────────────────

    context("copyOf") {
        test("full copy has same size and contents") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            val copy = arr.copyOf()
            copy.size shouldBe 3
            copy.contentEquals(arr) shouldBe true
        }

        test("mutating copy does not affect original") {
            val arr = Float16Array(2) { Float16(it.toFloat()) }
            val copy = arr.copyOf()
            copy[0] = Float16(99.0f)
            arr[0].toFloat() shouldBe 0.0f
        }

        test("copyOf with larger size pads with positive zero") {
            val arr = Float16Array(2) { Float16(it.toFloat()) }
            val copy = arr.copyOf(4)
            copy.size shouldBe 4
            copy[2].bits shouldBe 0.toShort()
            copy[3].bits shouldBe 0.toShort()
        }

        test("copyOf with smaller size truncates") {
            val arr = Float16Array(4) { Float16(it.toFloat()) }
            val copy = arr.copyOf(2)
            copy.size shouldBe 2
            copy[0].toFloat() shouldBe 0.0f
            copy[1].toFloat() shouldBe 1.0f
        }
    }

    context("copyOfRange") {
        test("copies the specified range") {
            val arr = Float16Array(4) { Float16(it.toFloat()) }
            val copy = arr.copyOfRange(1, 3)
            copy.size shouldBe 2
            copy[0].toFloat() shouldBe 1.0f
            copy[1].toFloat() shouldBe 2.0f
        }
    }

    // ── copyInto ─────────────────────────────────────────────────────────────

    context("copyInto") {
        test("copies all elements into destination at offset") {
            val src = Float16Array(2) { Float16(it.toFloat()) }
            val dst = Float16Array(4)
            src.copyInto(dst, destinationOffset = 1)
            dst[0].bits shouldBe 0.toShort()
            dst[1].toFloat() shouldBe 0.0f
            dst[2].toFloat() shouldBe 1.0f
            dst[3].bits shouldBe 0.toShort()
        }

        test("returns the destination array") {
            val src = Float16Array(1)
            val dst = Float16Array(1)
            src.copyInto(dst) shouldBe dst
        }
    }

    // ── toShortArray / ShortArray.toFloat16Array ──────────────────────────────

    context("toShortArray") {
        test("returned array has same bit patterns") {
            val arr = Float16Array(2) { Float16(it.toFloat()) }
            val shorts = arr.toShortArray()
            shorts[0] shouldBe arr[0].bits
            shorts[1] shouldBe arr[1].bits
        }

        test("mutating returned array does not affect original") {
            val arr = Float16Array(2) { Float16(it.toFloat()) }
            val shorts = arr.toShortArray()
            shorts[0] = 0x7FFF.toShort()
            arr[0].bits shouldNotBe 0x7FFF.toShort()
        }
    }

    context("ShortArray.toFloat16Array") {
        test("produces array with same bit patterns") {
            val shorts = shortArrayOf(0x3C00.toShort(), 0x4000.toShort())
            val arr = shorts.toFloat16Array()
            arr[0].bits shouldBe 0x3C00.toShort()
            arr[1].bits shouldBe 0x4000.toShort()
        }

        test("mutating source after conversion does not affect result") {
            val shorts = shortArrayOf(0x3C00.toShort())
            val arr = shorts.toFloat16Array()
            shorts[0] = 0
            arr[0].bits shouldBe 0x3C00.toShort()
        }
    }

    // ── sort ──────────────────────────────────────────────────────────────────

    context("sort") {
        test("sorts finite values in ascending numeric order") {
            val arr = Float16Array(4)
            arr[0] = Float16(3.0f); arr[1] = Float16(-1.0f)
            arr[2] = Float16(2.0f); arr[3] = Float16(0.0f)
            arr.sort()
            val values = (0 until arr.size).map { arr[it].toFloat() }
            values shouldBe listOf(-1.0f, 0.0f, 2.0f, 3.0f)
        }

        test("places negative values before positive values") {
            val arr = Float16Array(2)
            arr[0] = Float16(1.0f); arr[1] = Float16(-1.0f)
            arr.sort()
            arr[0].toFloat() shouldBe -1.0f
            arr[1].toFloat() shouldBe 1.0f
        }

        test("sort with range only reorders the specified slice") {
            val arr = Float16Array(4)
            arr[0] = Float16(9.0f)
            arr[1] = Float16(3.0f); arr[2] = Float16(1.0f)
            arr[3] = Float16(8.0f)
            arr.sort(fromIndex = 1, toIndex = 3)
            arr[0].toFloat() shouldBe 9.0f
            arr[1].toFloat() shouldBe 1.0f
            arr[2].toFloat() shouldBe 3.0f
            arr[3].toFloat() shouldBe 8.0f
        }
    }

    // ── contentEquals / contentHashCode / contentToString ────────────────────

    context("contentEquals") {
        test("equal arrays are content-equal") {
            val a = Float16Array(2) { Float16(it.toFloat()) }
            val b = Float16Array(2) { Float16(it.toFloat()) }
            a.contentEquals(b) shouldBe true
        }

        test("arrays with different values are not content-equal") {
            val a = Float16Array(1); a[0] = Float16(1.0f)
            val b = Float16Array(1); b[0] = Float16(2.0f)
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: distinct NaN payloads are not equal") {
            val a = Float16Array(1); a[0] = Float16.NaN
            val b = Float16Array(1); b[0] = Float16(0x7C01.toShort())
            a.contentEquals(b) shouldBe false
        }

        test("uses bit equality: +0 and -0 are not equal") {
            val a = Float16Array(1); a[0] = Float16(0)
            val b = Float16Array(1); b[0] = -Float16(0)
            a.contentEquals(b) shouldBe false
        }
    }

    context("contentHashCode") {
        test("equal arrays have equal content hash codes") {
            val a = Float16Array(2) { Float16(it.toFloat()) }
            val b = Float16Array(2) { Float16(it.toFloat()) }
            a.contentHashCode() shouldBe b.contentHashCode()
        }
    }

    context("contentToString") {
        test("empty array produces empty brackets") {
            Float16Array(0).contentToString() shouldBe "[]"
        }

        test("single element") {
            val arr = Float16Array(1); arr[0] = Float16(1.0f)
            arr.contentToString() shouldBe "[1.0]"
        }

        test("multiple elements are comma-separated") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            arr.contentToString() shouldBe "[0.0, 1.0, 2.0]"
        }
    }

    // ── asList ────────────────────────────────────────────────────────────────

    context("asList") {
        test("size matches array") {
            Float16Array(3).asList().size shouldBe 3
        }

        test("element access matches array") {
            val arr = Float16Array(3) { Float16(it.toFloat()) }
            val list = arr.asList()
            for (i in 0 until arr.size) list[i].bits shouldBe arr[i].bits
        }

        test("is a live view: mutation of array is reflected in list") {
            val arr = Float16Array(2)
            val list = arr.asList()
            arr[0] = Float16(5.0f)
            list[0].toFloat() shouldBe 5.0f
        }
    }

    // ── destructuring ─────────────────────────────────────────────────────────

    context("destructuring") {
        test("components 1 through 5 correspond to indices 0 through 4") {
            val arr = Float16Array(5) { Float16(it.toFloat()) }
            val (a, b, c, d, e) = arr
            a.toFloat() shouldBe 0.0f
            b.toFloat() shouldBe 1.0f
            c.toFloat() shouldBe 2.0f
            d.toFloat() shouldBe 3.0f
            e.toFloat() shouldBe 4.0f
        }
    }
})
