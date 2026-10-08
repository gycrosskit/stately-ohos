package co.touchlab.stately.collections

import co.touchlab.stately.concurrency.Synchronizable
import co.touchlab.stately.concurrency.synchronize

class ConcurrentMutableList<E> internal constructor(rootArg: Synchronizable?, private val del: MutableList<E>) :
    ConcurrentMutableCollection<E>(rootArg, del), MutableList<E> {
    constructor() : this(null, mutableListOf())

    override fun get(index: Int): E = syncTarget.synchronize { del.get(index) }

    override fun indexOf(element: E): Int = syncTarget.synchronize { del.indexOf(element) }

    override fun lastIndexOf(element: E): Int = syncTarget.synchronize { del.lastIndexOf(element) }

    override fun add(index: Int, element: E) {
        syncTarget.synchronize { del.add(index, element) }
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        withElements(elements) { del.addAll(index, it) }

    override fun listIterator(): MutableListIterator<E> =
        syncTarget.synchronize { ConcurrentMutableListIterator(syncTarget, del.listIterator()) }

    override fun listIterator(index: Int): MutableListIterator<E> =
        syncTarget.synchronize { ConcurrentMutableListIterator(syncTarget, del.listIterator(index)) }

    override fun removeAt(index: Int): E = syncTarget.synchronize { del.removeAt(index) }

    override fun set(index: Int, element: E): E = syncTarget.synchronize { del.set(index, element) }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        syncTarget.synchronize { ConcurrentMutableList(syncTarget, del.subList(fromIndex, toIndex)) }

    // Compare snapshots outside the lock so comparing two collections cannot acquire roots in opposite order.
    override fun equals(other: Any?): Boolean = this === other || syncTarget.synchronize { del.toList() } == other
    override fun hashCode(): Int = syncTarget.synchronize { del.toList() }.hashCode()

    fun <R> block(f: (MutableList<E>) -> R): R = syncTarget.synchronize {
        val wrapper = MutableListWrapper(del)
        try { f(wrapper) } finally { wrapper._coll = null }
    }
}

internal class MutableListWrapper<E>(list: MutableList<E>) : MutableCollectionWrapper<E>(list),
    MutableList<E> {
    private val list: MutableList<E> get() = checkNotNull(_coll) as MutableList<E>
    override fun equals(other: Any?): Boolean = this === other || list == other
    override fun hashCode(): Int = list.hashCode()
    override fun get(index: Int): E = list.get(index)

    override fun indexOf(element: E): Int = list.indexOf(element)

    override fun lastIndexOf(element: E): Int = list.lastIndexOf(element)

    override fun add(index: Int, element: E) = list.add(index, element)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = list.addAll(index, elements)

    override fun listIterator(): MutableListIterator<E> = list.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = list.listIterator(index)

    override fun removeAt(index: Int): E = list.removeAt(index)

    override fun set(index: Int, element: E): E = list.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = list.subList(fromIndex, toIndex)
}