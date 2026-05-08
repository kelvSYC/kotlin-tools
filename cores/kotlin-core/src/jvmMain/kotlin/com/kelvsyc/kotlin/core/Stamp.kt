package com.kelvsyc.kotlin.core

import java.util.concurrent.locks.StampedLock

/**
 * A type-safe wrapper around the `long` stamps produced by [StampedLock].
 *
 * Using a raw [Long] for lock stamps is error-prone: any arbitrary number can be passed
 * where a stamp is expected. This value class provides compile-time safety at zero runtime
 * cost.
 */
@JvmInline
value class Stamp @PublishedApi internal constructor(val value: Long) {
    /** Returns `true` if this stamp represents a write lock. */
    fun isWriteLocked(): Boolean = StampedLock.isWriteLockStamp(value)

    /** Returns `true` if this stamp represents a read lock. */
    fun isReadLocked(): Boolean = StampedLock.isReadLockStamp(value)

    /** Returns `true` if this stamp represents a successful optimistic read. */
    fun isOptimisticRead(): Boolean = StampedLock.isOptimisticReadStamp(value)

    /** Returns `true` if this stamp represents any lock (read or write). */
    fun isLocked(): Boolean = StampedLock.isLockStamp(value)
}

/** Acquires the write lock, blocking until available. */
fun StampedLock.writeStamp(): Stamp = Stamp(writeLock())

/** Attempts to acquire the write lock without blocking. Returns `null` if unavailable. */
fun StampedLock.tryWriteStamp(): Stamp? = tryWriteLock().takeIf { it != 0L }?.let(::Stamp)

/** Acquires the read lock, blocking until available. */
fun StampedLock.readStamp(): Stamp = Stamp(readLock())

/** Attempts to acquire the read lock without blocking. Returns `null` if unavailable. */
fun StampedLock.tryReadStamp(): Stamp? = tryReadLock().takeIf { it != 0L }?.let(::Stamp)

/** Returns a stamp for an optimistic read, or `null` if the lock is currently write-locked. */
fun StampedLock.tryOptimisticReadStamp(): Stamp? = tryOptimisticRead().takeIf { it != 0L }?.let(::Stamp)

/** Validates that an optimistic read [stamp] is still current. */
fun StampedLock.validate(stamp: Stamp): Boolean = validate(stamp.value)

/** Releases the write lock identified by [stamp]. */
fun StampedLock.unlockWrite(stamp: Stamp): Unit = unlockWrite(stamp.value)

/** Releases the read lock identified by [stamp]. */
fun StampedLock.unlockRead(stamp: Stamp): Unit = unlockRead(stamp.value)

/** Releases a lock identified by [stamp] (read or write). */
fun StampedLock.unlock(stamp: Stamp): Unit = unlock(stamp.value)

/**
 * Attempts to convert [stamp] to a write lock.
 * Returns a new write [Stamp], or `null` if conversion failed.
 */
fun StampedLock.tryConvertToWriteStamp(stamp: Stamp): Stamp? =
    tryConvertToWriteLock(stamp.value).takeIf { it != 0L }?.let(::Stamp)

/**
 * Attempts to convert [stamp] to a read lock.
 * Returns a new read [Stamp], or `null` if conversion failed.
 */
fun StampedLock.tryConvertToReadStamp(stamp: Stamp): Stamp? =
    tryConvertToReadLock(stamp.value).takeIf { it != 0L }?.let(::Stamp)

/**
 * Attempts to convert [stamp] to an optimistic read.
 * Returns a new optimistic-read [Stamp], or `null` if conversion failed.
 */
fun StampedLock.tryConvertToOptimisticReadStamp(stamp: Stamp): Stamp? =
    tryConvertToOptimisticRead(stamp.value).takeIf { it != 0L }?.let(::Stamp)
