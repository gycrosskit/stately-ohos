package co.touchlab.stately.collections

import java.lang.management.ManagementFactory
import java.util.concurrent.FutureTask
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.*

class RootLockContractTest {
    @Test fun opposingBulkOperationsSnapshotBeforeTakingTheirOwnLock() {
        for (operation in listOf<(ConcurrentMutableList<Int>, Collection<Int>) -> Unit>(
            { target, source -> target.addAll(source) },
            { target, source -> target.containsAll(source) },
            { target, source -> target.removeAll(source) },
            { target, source -> target.retainAll(source) },
            { target, source -> target.addAll(0, source) },
        )) {
            val first = ConcurrentMutableList<Int>().apply { add(1) }
            val second = ConcurrentMutableList<Int>().apply { add(2) }
            val entered = CountDownLatch(2); val copied = CountDownLatch(2)
            fun input(source: Collection<Int>): Collection<Int> = object : Collection<Int> by source {
                override fun iterator(): Iterator<Int> {
                    entered.countDown(); check(entered.await(5, TimeUnit.SECONDS))
                    val snapshot = source.toList()
                    copied.countDown(); check(copied.await(5, TimeUnit.SECONDS))
                    return snapshot.iterator()
                }
            }
            parallel({ operation(first, input(second)) }, { operation(second, input(first)) })
        }
        val first = ConcurrentMutableMap<Int, Int>().apply { put(1, 1) }
        val second = ConcurrentMutableMap<Int, Int>().apply { put(2, 2) }
        val entered = CountDownLatch(2); val copied = CountDownLatch(2)
        fun input(source: Map<Int, Int>): Map<Int, Int> = object : Map<Int, Int> by source {
            override val entries: Set<Map.Entry<Int, Int>> get() {
                entered.countDown(); check(entered.await(5, TimeUnit.SECONDS))
                val snapshot = source.toMap().entries
                copied.countDown(); check(copied.await(5, TimeUnit.SECONDS))
                return snapshot
            }
        }
        parallel({ first.putAll(input(second)) }, { second.putAll(input(first)) })
    }

    private fun parallel(first: () -> Unit, second: () -> Unit) {
        val done = CountDownLatch(2)
        val errors = java.util.concurrent.ConcurrentLinkedQueue<Throwable>()
        for (operation in listOf(first, second)) Thread {
            try { operation() } catch (error: Throwable) { errors.add(error) } finally { done.countDown() }
        }.apply { isDaemon = true; start() }
        assertTrue(done.await(5, TimeUnit.SECONDS), "opposing bulk calls deadlocked")
        assertTrue(errors.isEmpty(), errors.toString())
    }

    @Test fun entryReadWriteAndNestedIteratorUseTheOriginalRootLock() {
        val map = ConcurrentMutableMap<Int, Int>().apply { put(1, 2) }
        val entry = map.entries.first()
        val reads = map.block {
            listOf(
                assertBlockedByOwnerLock(map) { assertEquals(1, entry.key) },
                assertBlockedByOwnerLock(map) { assertEquals(2, entry.value) },
            )
        }
        reads.forEach { it.get(5, TimeUnit.SECONDS) }
        val write = map.block { assertBlockedByOwnerLock(map) { assertEquals(2, entry.setValue(3)) } }
        write.get(5, TimeUnit.SECONDS)
        assertEquals(3, map[1])
        val list = ConcurrentMutableList<Int>().apply { addAll(listOf(1, 2, 3)) }
        val nestedIterator = list.subList(0, 3).subList(0, 2).listIterator()
        val next = list.block { assertBlockedByOwnerLock(list) { assertEquals(1, nestedIterator.next()) } }
        next.get(5, TimeUnit.SECONDS)
        val set = list.block { assertBlockedByOwnerLock(list) { nestedIterator.set(5) } }
        set.get(5, TimeUnit.SECONDS)
        assertEquals(5, list[0])
    }

    private fun assertBlockedByOwnerLock(owner: Any, operation: () -> Unit): FutureTask<Unit> {
        val result = FutureTask<Unit> { operation() }
        val worker = Thread(result).apply { isDaemon = true; start() }
        val threads = ManagementFactory.getThreadMXBean()
        val ownerThread = Thread.currentThread().id
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        // JVM Synchronizable uses synchronized(owner). Observe that exact monitor, not a scheduling delay.
        while (!result.isDone && System.nanoTime() < deadline) {
            val info = threads.getThreadInfo(worker.id)
            if (info?.lockOwnerId == ownerThread && info.lockInfo?.identityHashCode == System.identityHashCode(owner)) return result
            Thread.yield()
        }
        if (result.isDone) result.get() // Propagate worker assertions instead of losing them on another thread.
        fail("operation did not wait for the original root monitor")
    }

}
