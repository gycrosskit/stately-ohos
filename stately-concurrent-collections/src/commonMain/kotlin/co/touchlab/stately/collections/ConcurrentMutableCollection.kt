package co.touchlab.stately.collections

import co.touchlab.stately.concurrency.Synchronizable
import co.touchlab.stately.concurrency.synchronize

open class ConcurrentMutableCollection<E> internal constructor(rootArg: Synchronizable? = null, private val del: MutableCollection<E>) :
    Synchronizable(),
    MutableCollection<E> {

    internal val syncTarget: Synchronizable = rootArg ?: this

    override val size: Int
        get() = syncTarget.synchronize { del.size }

    override fun contains(element: E): Boolean = syncTarget.synchronize { del.contains(element) }

    override fun containsAll(elements: Collection<E>): Boolean = withElements(elements) { del.containsAll(it) }

    override fun isEmpty(): Boolean = syncTarget.synchronize { del.isEmpty() }

    override fun add(element: E): Boolean = syncTarget.synchronize { del.add(element) }

    override fun addAll(elements: Collection<E>): Boolean = withElements(elements) { del.addAll(it) }

    override fun clear() {
        syncTarget.synchronize { del.clear() }
    }

    override fun iterator(): MutableIterator<E> =
        syncTarget.synchronize { ConcurrentMutableIterator(syncTarget, del.iterator()) }

    override fun remove(element: E): Boolean = syncTarget.synchronize { del.remove(element) }

    override fun removeAll(elements: Collection<E>): Boolean = withElements(elements) { del.removeAll(it) }

    override fun retainAll(elements: Collection<E>): Boolean = withElements(elements) { del.retainAll(it) }

    // Never hold our root while reading another collection's root. Same-root views keep self operations atomic.
    internal fun snapshot(): List<E> = syncTarget.synchronize { del.toList() }

    @Suppress("UNCHECKED_CAST")
    internal fun <R> withElements(elements: Collection<E>, action: (List<E>) -> R): R {
        if (elements is ConcurrentMutableCollection<*> && elements.syncTarget === syncTarget) {
            return syncTarget.synchronize { action(elements.toList()) }
        }
        val snapshot = if (elements is ConcurrentMutableCollection<*>) (elements as ConcurrentMutableCollection<E>).snapshot() else elements.toList()
        return syncTarget.synchronize { action(snapshot) }
    }

    fun <R> blockCollection(f: (MutableCollection<E>) -> R): R = syncTarget.synchronize {
        val wrapper = MutableCollectionWrapper(del)
        try { f(wrapper) } finally { wrapper._coll = null }
    }
}

internal open class ConcurrentMutableIterator<E>(
    private val root: Synchronizable,
    private val del: MutableIterator<E>
) :
    Synchronizable(),
    MutableIterator<E> {
    override fun hasNext(): Boolean = root.synchronize { del.hasNext() }

    override fun next(): E = root.synchronize { del.next() }

    override fun remove() {
        root.synchronize { del.remove() }
    }
}

internal open class MutableCollectionWrapper<E>(internal var _coll: MutableCollection<E>?) : MutableCollection<E> {

    // Reference will fail when block is done. This is to prevent bad things in the block call function
    private val coll: MutableCollection<E>
        get() = checkNotNull(_coll) { "Collection block has ended" }

    override fun add(element: E): Boolean = coll.add(element)
    override fun addAll(elements: Collection<E>): Boolean = coll.addAll(elements)
    override fun clear() {
        coll.clear()
    }

    override fun iterator(): MutableIterator<E> = coll.iterator()
    override fun remove(element: E): Boolean = coll.remove(element)
    override fun removeAll(elements: Collection<E>): Boolean = coll.removeAll(elements)
    override fun retainAll(elements: Collection<E>): Boolean = coll.retainAll(elements)
    override val size: Int
        get() = coll.size

    override fun contains(element: E): Boolean = coll.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = coll.containsAll(elements)
    override fun isEmpty(): Boolean = coll.isEmpty()
}