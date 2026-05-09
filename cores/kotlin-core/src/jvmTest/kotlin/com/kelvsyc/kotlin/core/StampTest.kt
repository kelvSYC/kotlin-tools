package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.locks.StampedLock

class StampTest : FunSpec({

    context("write lock") {
        test("writeStamp acquires and unlockWrite releases") {
            val lock = StampedLock()
            val stamp = lock.writeStamp()
            stamp.isWriteLocked() shouldBe true
            stamp.isLocked() shouldBe true
            stamp.isReadLocked() shouldBe false
            stamp.isOptimisticRead() shouldBe false
            lock.isWriteLocked shouldBe true
            lock.unlockWrite(stamp)
            lock.isWriteLocked shouldBe false
        }

        test("tryWriteStamp succeeds when unlocked") {
            val lock = StampedLock()
            val stamp = lock.tryWriteStamp()
            stamp shouldNotBe null
            stamp!!.isWriteLocked() shouldBe true
            lock.unlockWrite(stamp)
        }

        test("tryWriteStamp returns null when write-locked") {
            val lock = StampedLock()
            val held = lock.writeStamp()
            lock.tryWriteStamp() shouldBe null
            lock.unlockWrite(held)
        }
    }

    context("read lock") {
        test("readStamp acquires and unlockRead releases") {
            val lock = StampedLock()
            val stamp = lock.readStamp()
            stamp.isReadLocked() shouldBe true
            stamp.isLocked() shouldBe true
            stamp.isWriteLocked() shouldBe false
            lock.readLockCount shouldBe 1
            lock.unlockRead(stamp)
            lock.readLockCount shouldBe 0
        }

        test("tryReadStamp succeeds when unlocked") {
            val lock = StampedLock()
            val stamp = lock.tryReadStamp()
            stamp shouldNotBe null
            stamp!!.isReadLocked() shouldBe true
            lock.unlockRead(stamp)
        }

        test("tryReadStamp returns null when write-locked") {
            val lock = StampedLock()
            val held = lock.writeStamp()
            lock.tryReadStamp() shouldBe null
            lock.unlockWrite(held)
        }
    }

    context("optimistic read") {
        test("tryOptimisticReadStamp succeeds when unlocked") {
            val lock = StampedLock()
            val stamp = lock.tryOptimisticReadStamp()
            stamp shouldNotBe null
            stamp!!.isOptimisticRead() shouldBe true
            stamp.isLocked() shouldBe false
        }

        test("validate returns true when no write has occurred") {
            val lock = StampedLock()
            val stamp = lock.tryOptimisticReadStamp()!!
            lock.validate(stamp) shouldBe true
        }

        test("validate returns false after a write") {
            val lock = StampedLock()
            val stamp = lock.tryOptimisticReadStamp()!!
            val ws = lock.writeStamp()
            lock.unlockWrite(ws)
            lock.validate(stamp) shouldBe false
        }

        test("tryOptimisticReadStamp returns null when write-locked") {
            val lock = StampedLock()
            val held = lock.writeStamp()
            lock.tryOptimisticReadStamp() shouldBe null
            lock.unlockWrite(held)
        }
    }

    context("generic unlock") {
        test("unlock releases a write lock") {
            val lock = StampedLock()
            val stamp = lock.writeStamp()
            lock.unlock(stamp)
            lock.isWriteLocked shouldBe false
        }

        test("unlock releases a read lock") {
            val lock = StampedLock()
            val stamp = lock.readStamp()
            lock.unlock(stamp)
            lock.readLockCount shouldBe 0
        }
    }

    context("stamp conversion") {
        test("tryConvertToWriteStamp from read lock") {
            val lock = StampedLock()
            val readStamp = lock.readStamp()
            val writeStamp = lock.tryConvertToWriteStamp(readStamp)
            writeStamp shouldNotBe null
            writeStamp!!.isWriteLocked() shouldBe true
            lock.unlockWrite(writeStamp)
        }

        test("tryConvertToReadStamp from write lock") {
            val lock = StampedLock()
            val writeStamp = lock.writeStamp()
            val readStamp = lock.tryConvertToReadStamp(writeStamp)
            readStamp shouldNotBe null
            readStamp!!.isReadLocked() shouldBe true
            lock.unlockRead(readStamp)
        }

        test("tryConvertToOptimisticReadStamp from read lock") {
            val lock = StampedLock()
            val readStamp = lock.readStamp()
            val optStamp = lock.tryConvertToOptimisticReadStamp(readStamp)
            optStamp shouldNotBe null
            optStamp!!.isOptimisticRead() shouldBe true
        }
    }
})
