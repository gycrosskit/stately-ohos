package co.touchlab.stately.collections

import co.touchlab.stately.concurrency.Synchronizable
import co.touchlab.stately.concurrency.synchronize

class ConcurrentMutableSet<E> internal constructor(rootArg: Synchronizable?, private val del: MutableSet<E>) :
    ConcurrentMutableCollection<E>(rootArg, del),
    MutableSet<E> {
    constructor() : this(null, mutableSetOf())

    // Compare snapshots outside the lock so comparing two collections cannot acquire roots in opposite order.
    override fun equals(other: Any?): Boolean = this === other || syncTarget.synchronize { del.toSet() } == other
    override fun hashCode(): Int = syncTarget.synchronize { del.toSet() }.hashCode()

    fun <R> block(f: (MutableSet<E>) -> R): R = syncTarget.synchronize {
        val wrapper = MutableSetWrapper(del)
        try { f(wrapper) } finally { wrapper._coll = null }
    }
}

internal class MutableSetWrapper<E>(set: MutableSet<E>) : MutableCollectionWrapper<E>(set), MutableSet<E> {
    override fun equals(other: Any?): Boolean = this === other || checkNotNull(_coll) == other
    override fun hashCode(): Int = checkNotNull(_coll).hashCode()
}