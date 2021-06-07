package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap
import kotlin.collections.MutableMap.MutableEntry

/**
 * A Map wrapper class that implements observability.
 */
open class ObservableMapWrapper<K, V>(private val backingMap: MutableMap<K, V>) : ObservableMap<K, V> {

    private lateinit var entrySet: ObservableEntrySet

    private lateinit var keySet: ObservableKeySet

    private lateinit var valueCollection: ObservableValues

    private var listenerHelper: MapListenerHelper<K, V>? = null

    private inner class SimpleChange(private val keyState: K, private val old: V?, private val added: V?,
            private val wasAddedState: Boolean, private val wasRemovedState: Boolean) : Change<K, V>(this) {

        init {
            check(this.wasAddedState || this.wasRemovedState)
        }

        override val wasAdded: Boolean
            get() = this.wasAddedState

        override val wasRemoved: Boolean
            get() = this.wasRemovedState

        override val key: K
            get() = this.keyState

        override val valueAdded: V?
            get() = this.added

        override val valueRemoved: V?
            get() = this.old

        override fun toString(): String {
            val builder = StringBuilder()
            if (this.wasAddedState) {
                if (this.wasRemovedState) {
                    builder.append("replaced ").append(this.old).append(" by ").append(this.added)
                } else {
                    builder.append("added ").append(this.added)
                }
            } else {
                builder.append("removed ").append(this.old)
            }
            builder.append(" at key ").append(this.keyState)
            return builder.toString()
        }

    }

    protected fun callObservers(change: Change<K, V>) {
        MapListenerHelper.fireValueChangedEvent(this.listenerHelper, change)
    }

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        return this.listenerHelper != null && this.listenerHelper!!.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: MapChangeListener<in K, in V>) {
        if (!isMapChangeListenerAlreadyAdded(listener)) {
            this.listenerHelper = MapListenerHelper.addListener(this.listenerHelper, listener)
        }
    }

    override fun removeListener(listener: MapChangeListener<in K, in V>) {
        if (isMapChangeListenerAlreadyAdded(listener)) {
            this.listenerHelper = MapListenerHelper.removeListener(this.listenerHelper, listener)
        }
    }

    override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
        return this.listenerHelper != null && this.listenerHelper!!.mapChangeListeners.contains(listener)
    }

    override val size: Int
        get() = this.backingMap.size

    override fun isEmpty(): Boolean {
        return this.backingMap.isEmpty()
    }

    override fun containsKey(key: K): Boolean {
        return this.backingMap.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return this.backingMap.containsValue(value)
    }

    override fun get(key: K): V? {
        return this.backingMap[key]
    }

    override fun put(key: K, value: V): V? {
        val ret: V?
        if (this.backingMap.contains(key)) {
            ret = this.backingMap.put(key, value)
            if (ret != value) {
                callObservers(SimpleChange(key, ret, value, wasAddedState = true, wasRemovedState = true))
            }
        } else {
            ret = this.backingMap.put(key, value)
            callObservers(SimpleChange(key, ret, value, wasAddedState = true, wasRemovedState = false))
        }
        return ret
    }

    override fun remove(key: K): V? {
        if (!this.backingMap.contains(key)) {
            return null
        }
        val ret = this.backingMap.remove(key)
        callObservers(SimpleChange(key, ret, null, wasAddedState = false, wasRemovedState = true))
        return ret
    }

    override fun putAll(from: Map<out K, V>) {
        for (e in from) {
            put(e.key, e.value)
        }
    }

    override fun clear() {
        val i = this.backingMap.entries.iterator()
        while (i.hasNext()) {
            val e = i.next()
            val key = e.key
            val value = e.value
            i.remove()
            callObservers(SimpleChange(key, value, null, wasAddedState = false, wasRemovedState = true))
        }
    }

    override val keys: MutableSet<K>
        get() {
            if (!this::keySet.isInitialized) {
                this.keySet = ObservableKeySet()
            }
            return this.keySet
        }

    override val values: MutableCollection<V>
        get() {
            if (!this::valueCollection.isInitialized) {
                this.valueCollection = ObservableValues()
            }
            return this.valueCollection
        }

    override val entries: MutableSet<MutableEntry<K, V>>
        get() {
            if (!this::entrySet.isInitialized) {
                this.entrySet = ObservableEntrySet()
            }
            return this.entrySet
        }

    override fun toString(): String {
        return this.backingMap.toString()
    }

    override fun equals(other: Any?): Boolean {
        return this.backingMap.equals(other)
    }

    override fun hashCode(): Int {
        return this.backingMap.hashCode()
    }

    private inner class ObservableKeySet : MutableSet<K> {

        override val size: Int
            get() = this@ObservableMapWrapper.backingMap.size

        override fun isEmpty(): Boolean {
            return this@ObservableMapWrapper.backingMap.isEmpty()
        }

        override fun contains(element: K): Boolean {
            return this@ObservableMapWrapper.backingMap.contains(element)
        }

        override fun iterator(): MutableIterator<K> {
            return object : MutableIterator<K> {

                val entryIt = this@ObservableMapWrapper.backingMap.entries.iterator()

                var lastKey: K? = null

                var lastValue: V? = null

                override fun hasNext(): Boolean {
                    return this.entryIt.hasNext()
                }

                override fun next(): K {
                    val last = this.entryIt.next()
                    this.lastKey = last.key
                    this.lastValue = last.value
                    return last.key
                }

                override fun remove() {
                    this.entryIt.remove()
                    callObservers(SimpleChange(this.lastKey!!, this.lastValue, null, wasAddedState = false,
                            wasRemovedState = true))
                }

            }
        }

        override fun add(element: K): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun remove(element: K): Boolean {
            val res = this@ObservableMapWrapper.contains(element)
            if (res) {
                this@ObservableMapWrapper.remove(element)
            }
            return res
        }

        override fun containsAll(elements: Collection<K>): Boolean {
            return this@ObservableMapWrapper.backingMap.keys.containsAll(elements)
        }

        override fun addAll(elements: Collection<K>): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun retainAll(elements: Collection<K>): Boolean {
            return removeRetain(elements, false)
        }

        private fun removeRetain(c: Collection<K>, remove: Boolean): Boolean {
            var removed = false
            val i = this@ObservableMapWrapper.backingMap.entries.iterator()
            while (i.hasNext()) {
                val e = i.next()
                if (remove == c.contains(e.key)) {
                    removed = true
                    val key = e.key
                    val value = e.value
                    i.remove()
                    callObservers(SimpleChange(key, value, null, wasAddedState = false, wasRemovedState = true))
                }
            }
            return removed
        }

        override fun removeAll(elements: Collection<K>): Boolean {
            return removeRetain(elements, true)
        }

        override fun clear() {
            this@ObservableMapWrapper.clear()
        }

        override fun toString(): String {
            return this@ObservableMapWrapper.backingMap.keys.toString()
        }

        override fun equals(other: Any?): Boolean {
            return this@ObservableMapWrapper.backingMap.keys == other
        }

        override fun hashCode(): Int {
            return this@ObservableMapWrapper.backingMap.keys.hashCode()
        }

    }

    private inner class ObservableValues : MutableCollection<V> {

        override val size: Int
            get() = this@ObservableMapWrapper.backingMap.size

        override fun isEmpty(): Boolean {
            return this@ObservableMapWrapper.backingMap.isEmpty()
        }

        override fun contains(element: V): Boolean {
            return this@ObservableMapWrapper.backingMap.values.contains(element)
        }

        override fun iterator(): MutableIterator<V> {
            return object : MutableIterator<V> {

                val entryIt = this@ObservableMapWrapper.backingMap.entries.iterator()

                var lastKey: K? = null

                var lastValue: V? = null

                override fun hasNext(): Boolean {
                    return this.entryIt.hasNext()
                }

                override fun next(): V {
                    val last = this.entryIt.next()
                    this.lastKey = last.key
                    this.lastValue = last.value
                    return last.value
                }

                override fun remove() {
                    this.entryIt.remove()
                    callObservers(SimpleChange(this.lastKey!!, this.lastValue, null, wasAddedState = false,
                            wasRemovedState = true))
                }

            }
        }

        override fun add(element: V): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun remove(element: V): Boolean {
            val i = iterator()
            while (i.hasNext()) {
                if (i.next() == element) {
                    i.remove()
                    return true
                }
            }
            return false
        }

        override fun containsAll(elements: Collection<V>): Boolean {
            return this@ObservableMapWrapper.backingMap.values.containsAll(elements)
        }

        override fun addAll(elements: Collection<V>): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun removeAll(elements: Collection<V>): Boolean {
            return removeRetain(elements, true)
        }

        private fun removeRetain(c: Collection<V>, remove: Boolean): Boolean {
            var removed = false
            val i = this@ObservableMapWrapper.backingMap.entries.iterator()
            while (i.hasNext()) {
                val e = i.next()
                if (remove == c.contains(e.value)) {
                    removed = true
                    val key = e.key
                    val value = e.value
                    i.remove()
                    callObservers(SimpleChange(key, value, null, wasAddedState = false, wasRemovedState = true))
                }
            }
            return removed
        }

        override fun retainAll(elements: Collection<V>): Boolean {
            return removeRetain(elements, false)
        }

        override fun clear() {
            this@ObservableMapWrapper.clear()
        }

        override fun toString(): String {
            return this@ObservableMapWrapper.backingMap.values.toString()
        }

        override fun equals(other: Any?): Boolean {
            return this@ObservableMapWrapper.backingMap.values == other
        }

        override fun hashCode(): Int {
            return this@ObservableMapWrapper.backingMap.hashCode()
        }

    }

    private inner class ObservableEntry(private val backingEntry: MutableEntry<K, V>) : MutableEntry<K, V> {

        override val key: K
            get() = this.backingEntry.key

        override val value: V
            get() = this.backingEntry.value

        override fun setValue(newValue: V): V {
            val oldValue = this.backingEntry.setValue(newValue)
            callObservers(SimpleChange(this.key, oldValue, newValue, wasAddedState = true, wasRemovedState = true))
            return oldValue
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) {
                return false
            }
            return this.key == other.key && this.value == other.value
        }

        override fun hashCode(): Int {
            return (this.key?.hashCode() ?: 0) xor (this.value?.hashCode() ?: 0)
        }

        override fun toString(): String {
            return "${this.key}=${this.value}"
        }

    }

    private inner class ObservableEntrySet : MutableSet<MutableEntry<K, V>> {

        override val size: Int
            get() = this@ObservableMapWrapper.backingMap.size

        override fun isEmpty(): Boolean {
            return this@ObservableMapWrapper.backingMap.isEmpty()
        }

        override fun contains(element: MutableEntry<K, V>): Boolean {
            return this@ObservableMapWrapper.backingMap.entries.contains(element)
        }

        override fun iterator(): MutableIterator<MutableEntry<K, V>> {
            return object : MutableIterator<MutableEntry<K, V>> {

                val entryIt = this@ObservableMapWrapper.backingMap.entries.iterator()

                var lastKey: K? = null

                var lastValue: V? = null

                override fun hasNext(): Boolean {
                    return this.entryIt.hasNext()
                }

                override fun next(): MutableEntry<K, V> {
                    val last = this.entryIt.next()
                    this.lastKey = last.key
                    this.lastValue = last.value
                    return ObservableEntry(last)
                }

                override fun remove() {
                    this@ObservableMapWrapper.backingMap.entries.toTypedArray()
                    this.entryIt.remove()
                    callObservers(SimpleChange(this.lastKey!!, this.lastValue, null, wasAddedState = false,
                            wasRemovedState = true))
                }

            }
        }

        override fun add(element: MutableEntry<K, V>): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun remove(element: MutableEntry<K, V>): Boolean {
            val ret = this@ObservableMapWrapper.backingMap.entries.remove(element)
            if (ret) {
                callObservers(SimpleChange(element.key, element.value, null, wasAddedState = false,
                        wasRemovedState = true))
            }
            return ret
        }

        override fun containsAll(elements: Collection<MutableEntry<K, V>>): Boolean {
            return this@ObservableMapWrapper.backingMap.entries.containsAll(elements)
        }

        override fun addAll(elements: Collection<MutableEntry<K, V>>): Boolean {
            throw UnsupportedOperationException("Not supported.")
        }

        override fun retainAll(elements: Collection<MutableEntry<K, V>>): Boolean {
            return removeRetain(elements, false)
        }

        private fun removeRetain(c: Collection<MutableEntry<K, V>>, remove: Boolean): Boolean {
            var removed = false
            val i = this@ObservableMapWrapper.backingMap.entries.iterator()
            while (i.hasNext()) {
                val e = i.next()
                if (remove == c.contains(e)) {
                    removed = true
                    val key = e.key
                    val value = e.value
                    i.remove()
                    callObservers(SimpleChange(key, value, null, wasAddedState = false, wasRemovedState = true))
                }
            }
            return removed
        }

        override fun removeAll(elements: Collection<MutableEntry<K, V>>): Boolean {
            return removeRetain(elements, true)
        }

        override fun clear() {
            this@ObservableMapWrapper.clear()
        }

        override fun toString(): String {
            return this@ObservableMapWrapper.backingMap.entries.toString()
        }

        override fun equals(other: Any?): Boolean {
            return this@ObservableMapWrapper.backingMap.entries == other
        }

        override fun hashCode(): Int {
            return this@ObservableMapWrapper.backingMap.entries.hashCode()
        }

    }

}