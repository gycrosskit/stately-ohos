package co.touchlab.stately.collections

import co.touchlab.stately.concurrency.Synchronizable
import co.touchlab.stately.concurrency.synchronize
import kotlin.jvm.JvmName

class ConcurrentMutableMap<K, V> internal constructor(
    rootArg: Synchronizable? = null,
    private val del: MutableMap<K, V>
) : Synchronizable(), MutableMap<K, V> {

    constructor() : this(null, mutableMapOf())

    private val syncTarget: Synchronizable = rootArg ?: this

    override val size: Int
        get() = syncTarget.synchronize { del.size }
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = syncTarget.synchronize {
            object : MutableSet<MutableMap.MutableEntry<K, V>> by ConcurrentMutableSet(syncTarget, del.entries) {
                override fun equals(other: Any?): Boolean = this === other || syncTarget.synchronize { del.toMap().entries } == other
                override fun hashCode(): Int = syncTarget.synchronize { del.toMap().entries }.hashCode()
                override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = syncTarget.synchronize {
                    val iterator = ConcurrentMutableIterator(syncTarget, del.entries.iterator())
                    object : MutableIterator<MutableMap.MutableEntry<K, V>> by iterator {
                        override fun next(): MutableMap.MutableEntry<K, V> = ConcurrentMutableMapEntry(syncTarget, iterator.next())
                    }
                }
            }
        }
    override val keys: MutableSet<K>
        get() = syncTarget.synchronize { ConcurrentMutableSet(syncTarget, del.keys) }
    override val values: MutableCollection<V>
        get() = syncTarget.synchronize { ConcurrentMutableCollection(syncTarget, del.values) }

    override fun containsKey(key: K): Boolean = syncTarget.synchronize { del.containsKey(key) }
    override fun containsValue(value: V): Boolean = syncTarget.synchronize { del.containsValue(value) }
    override fun get(key: K): V? = syncTarget.synchronize { del.get(key) }
    override fun isEmpty(): Boolean = syncTarget.synchronize { del.isEmpty() }
    override fun clear() {
        syncTarget.synchronize { del.clear() }
    }

    /**
     * If the specified key is not already associated with a value
     * attempts to compute its value using the given mapping function and enters it into this map
     */
    @JvmName("safeComputeIfAbsent")
    fun computeIfAbsent(key: K, defaultValue: (K) -> V): V {
        return syncTarget.synchronize {
            val value = del[key]
            if (value == null) {
                val newValue = defaultValue(key)
                del[key] = newValue
                newValue
            } else {
                value
            }
        }
    }

    override fun put(key: K, value: V): V? = syncTarget.synchronize { del.put(key, value) }
    @Suppress("UNCHECKED_CAST")
    override fun putAll(from: Map<out K, V>) {
        if (from === this) return
        val snapshot = if (from is ConcurrentMutableMap<*, *>) from.snapshot() as Map<out K, V> else from.toMap()
        syncTarget.synchronize { del.putAll(snapshot) }
    }

    override fun remove(key: K): V? = syncTarget.synchronize { del.remove(key) }

    private fun snapshot(): Map<K, V> = syncTarget.synchronize { del.toMap() }

    // Compare snapshots outside the lock so comparing two collections cannot acquire roots in opposite order.
    override fun equals(other: Any?): Boolean = this === other || snapshot() == other
    override fun hashCode(): Int = snapshot().hashCode()

    fun <R> block(f: (MutableMap<K, V>) -> R): R = syncTarget.synchronize {
        val wrapper = MutableMapWrapper(del)
        try { f(wrapper) } finally { wrapper.delegate = null }
    }
}

internal class ConcurrentMutableListIterator<E>(
    private val root: Synchronizable,
    private val del: MutableListIterator<E>
) :
    ConcurrentMutableIterator<E>(root, del),
    MutableListIterator<E> {
    override fun hasPrevious(): Boolean = root.synchronize { del.hasPrevious() }

    override fun nextIndex(): Int = root.synchronize { del.nextIndex() }

    override fun previous(): E = root.synchronize { del.previous() }

    override fun previousIndex(): Int = root.synchronize { del.previousIndex() }

    override fun add(element: E) {
        root.synchronize { del.add(element) }
    }

    override fun set(element: E) {
        root.synchronize { del.set(element) }
    }
}

internal class MutableMapWrapper<K, V>(internal var delegate: MutableMap<K, V>?) : MutableMap<K, V> {
    private val map: MutableMap<K, V> get() = checkNotNull(delegate) { "Map block has ended" }
    override fun equals(other: Any?): Boolean = this === other || map == other
    override fun hashCode(): Int = map.hashCode()
    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: V): Boolean = map.containsValue(value)

    override fun get(key: K): V? = map.get(key)

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries
    override val keys: MutableSet<K>
        get() = map.keys
    override val values: MutableCollection<V>
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun put(key: K, value: V): V? = map.put(key, value)

    @Suppress("UNCHECKED_CAST")
    override fun putAll(from: Map<out K, V>) {
        map.putAll(from)
    }

    override fun remove(key: K): V? = map.remove(key)
}

private class ConcurrentMutableMapEntry<K, V>(
    private val root: Synchronizable,
    private val entry: MutableMap.MutableEntry<K, V>,
) : MutableMap.MutableEntry<K, V> {
    override val key: K get() = root.synchronize { entry.key }
    override val value: V get() = root.synchronize { entry.value }
    override fun setValue(newValue: V): V = root.synchronize { entry.setValue(newValue) }
    override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && key == other.key && value == other.value
    override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)
    override fun toString(): String = "$key=$value"
}
