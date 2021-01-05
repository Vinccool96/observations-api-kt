package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.collections.WeakMapChangeListener
import java.util.*

/**
 * ObservableMap wrapper that does not allow changes to the underlying container.
 */
class UnmodifiableObservableMap<K, V>(map: ObservableMap<K, V>) : AbstractMutableMap<K, V>(), ObservableMap<K, V> {

    private val backingMap: ObservableMap<K, V> = map

    private var listenerHelper: MapListenerHelper<K, V>? = null

    private val listener: MapChangeListener<K, V> = MapChangeListener { change ->
        callObservers(MapAdapterChange(this, change))
    }

    private lateinit var keySet: MutableSet<K>

    private lateinit var valueSet: MutableCollection<V>

    private lateinit var entrySet: MutableSet<MutableMap.MutableEntry<K, V>>

    init {
        this.backingMap.addListener(WeakMapChangeListener(this.listener))
    }

    private fun callObservers(c: Change<out K, out V>) {
        MapListenerHelper.fireValueChangedEvent(this.listenerHelper, c)
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
        val curHelper = this.listenerHelper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
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
        val curHelper = this.listenerHelper
        return curHelper != null && curHelper.mapChangeListeners.contains(listener)
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
        throw UnsupportedOperationException()
    }

    @get:ReturnsUnmodifiableCollection
    override val keys: MutableSet<K>
        get() {
            if (!this::keySet.isInitialized) {
                this.keySet = Collections.unmodifiableSet(this.backingMap.keys)
            }
            return this.keySet
        }

    @get:ReturnsUnmodifiableCollection
    override val values: MutableCollection<V>
        get() {
            if (!this::valueSet.isInitialized) {
                this.valueSet = Collections.unmodifiableCollection(this.backingMap.values)
            }
            return this.valueSet
        }

    @get:ReturnsUnmodifiableCollection
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            if (!this::entrySet.isInitialized) {
                this.entrySet = Collections.unmodifiableMap(this.backingMap).entries
            }
            return this.entrySet
        }

}