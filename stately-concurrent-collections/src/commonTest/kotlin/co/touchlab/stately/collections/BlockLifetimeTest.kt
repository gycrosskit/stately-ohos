package co.touchlab.stately.collections

import kotlin.test.*

class BlockLifetimeTest {
    @Test fun bulkSelfAndSameRootViewsKeepCollectionSemantics() {
        val list = ConcurrentMutableList<Int>().apply { addAll(listOf(1, 2)) }
        assertTrue(list.containsAll(list)); assertFalse(list.retainAll(list))
        assertTrue(list.addAll(list)); assertEquals(listOf(1, 2, 1, 2), list)
        assertTrue(list.addAll(0, list.subList(0, 2)))
        assertEquals(listOf(1, 2, 1, 2, 1, 2), list)
        assertTrue(list.removeAll(list)); assertTrue(list.isEmpty())
        val set = ConcurrentMutableSet<Int>().apply { addAll(listOf(1, 2)) }
        assertFalse(set.addAll(set)); assertFalse(set.retainAll(set)); assertTrue(set.removeAll(set))
        val map = ConcurrentMutableMap<Int, Int>().apply { put(1, 2) }
        map.putAll(map); assertEquals(mapOf(1 to 2), map)
    }

    @Test fun collectionEqualityAndHashFollowKotlinContracts() {
        val list = ConcurrentMutableList<Int>().apply { addAll(listOf(1, 2)) }
        val set = ConcurrentMutableSet<Int>().apply { addAll(listOf(1, 2)) }
        val map = ConcurrentMutableMap<Int, Int>().apply { put(1, 2) }
        assertEquals(listOf(1, 2), list); assertEquals(list, listOf(1, 2))
        assertEquals(setOf(1, 2), set); assertEquals(set, setOf(1, 2))
        assertEquals(mapOf(1 to 2), map); assertEquals(map, mapOf(1 to 2))
        assertEquals(listOf(1, 2).hashCode(), list.hashCode())
        assertEquals(setOf(1, 2).hashCode(), set.hashCode())
        assertEquals(mapOf(1 to 2).hashCode(), map.hashCode())
        assertEquals(mapOf(1 to 2).entries, map.entries)
        assertEquals(map.entries, mapOf(1 to 2).entries)
        assertEquals(mapOf(1 to 2).entries.hashCode(), map.entries.hashCode())
        list.block { assertEquals(listOf(1, 2), it); assertEquals(it, listOf(1, 2)); assertEquals(list.hashCode(), it.hashCode()) }
        set.block { assertEquals(setOf(1, 2), it); assertEquals(it, setOf(1, 2)); assertEquals(set.hashCode(), it.hashCode()) }
        map.block { assertEquals(mapOf(1 to 2), it); assertEquals(it, mapOf(1 to 2)); assertEquals(map.hashCode(), it.hashCode()) }
        val entry = map.entries.first()
        assertEquals(mapOf(1 to 2).entries.first(), entry)
        assertEquals(mapOf(1 to 2).entries.first().hashCode(), entry.hashCode())
        assertEquals("1=2", entry.toString())
        assertEquals(2, entry.setValue(3)); assertEquals(3, map[1])
    }

    @Test fun blockWrappersExpireOnSuccessAndFailure() {
        val list = ConcurrentMutableList<Int>().apply { add(1) }
        val set = ConcurrentMutableSet<Int>().apply { add(1) }
        val map = ConcurrentMutableMap<Int, Int>().apply { put(1, 1) }
        for (fail in listOf(false, true)) {
            var escapedList: MutableList<Int>? = null
            var escapedSet: MutableSet<Int>? = null
            var escapedMap: MutableMap<Int, Int>? = null
            var escapedCollection: MutableCollection<Int>? = null
            runCatching { list.block { escapedList = it; if (fail) error("fixture") } }
            runCatching { set.block { escapedSet = it; if (fail) error("fixture") } }
            runCatching { map.block { escapedMap = it; if (fail) error("fixture") } }
            runCatching { list.blockCollection { escapedCollection = it; if (fail) error("fixture") } }
            assertFailsWith<IllegalStateException> { escapedList!!.add(2) }
            assertFailsWith<IllegalStateException> { escapedList!![0] = 2 }
            assertFailsWith<IllegalStateException> { escapedSet!!.add(2) }
            assertFailsWith<IllegalStateException> { escapedMap!![2] = 2 }
            assertFailsWith<IllegalStateException> { escapedCollection!!.add(2) }
            assertEquals(listOf(1), list.toList()); assertEquals(setOf(1), set.toSet()); assertEquals(1, map.size)
        }
    }

    @Test fun nestedViewsShareOriginalSynchronizationTarget() {
        val list = ConcurrentMutableList<Int>().apply { addAll(listOf(1, 2, 3)) }
        val sub = list.subList(0, 3) as ConcurrentMutableList<Int>
        val nested = sub.subList(0, 2) as ConcurrentMutableList<Int>
        assertSame(list.syncTarget, sub.syncTarget)
        assertSame(list.syncTarget, nested.syncTarget)
        nested.listIterator().apply { next(); set(5) }
        assertEquals(5, list[0])
    }
}
