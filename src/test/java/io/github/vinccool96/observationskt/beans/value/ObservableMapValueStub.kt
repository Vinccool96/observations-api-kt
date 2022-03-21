package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.MapExpressionHelper

@Suppress("PrivatePropertyName")
class ObservableMapValueStub<K, V>(private var valueState: ObservableMap<K, V>?) : ObservableMapValue<K, V> {

    private val EMPTY_MAP: ObservableMap<K, V> = EmptyObservableMap()

    constructor() : this(null)

    fun set(value: ObservableMap<K, V>?) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override fun get(): ObservableMap<K, V>? {
        return this.valueState
    }

    override val value: ObservableMap<K, V>?
        get() = this.valueState

    private var helper: MapExpressionHelper<K, V>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableMap<K, V>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: MapChangeListener<in K, in V>) {
        if (!isMapChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: MapChangeListener<in K, in V>) {
        if (isMapChangeListenerAlreadyAdded(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.mapChangeListeners.contains(listener)
    }

    private fun fireValueChangedEvent() {
        MapExpressionHelper.fireValueChangedEvent(this.helper)
    }

    override val size: Int
        get() = (get() ?: EMPTY_MAP).size

    override fun isEmpty(): Boolean {
        return (get() ?: EMPTY_MAP).isEmpty()
    }

    override fun containsKey(key: K): Boolean {
        return (get() ?: EMPTY_MAP).containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return (get() ?: EMPTY_MAP).containsValue(value)
    }

    override fun put(key: K, value: V): V? {
        return (get() ?: EMPTY_MAP).put(key, value)
    }

    override fun remove(key: K): V? {
        return (get() ?: EMPTY_MAP).remove(key)
    }

    override fun putAll(from: Map<out K, V>) {
        (get() ?: EMPTY_MAP).putAll(from)
    }

    override fun clear() {
        (get() ?: EMPTY_MAP).clear()
    }

    override fun setAll(vararg pairs: Pair<K, V>) {
        (get() ?: EMPTY_MAP).setAll(*pairs)
    }

    override fun setAll(map: Map<out K, V>) {
        (get() ?: EMPTY_MAP).setAll(map)
    }

    override val keys: MutableSet<K>
        get() = (get() ?: EMPTY_MAP).keys

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = (get() ?: EMPTY_MAP).entries

    override val values: MutableCollection<V>
        get() = (get() ?: EMPTY_MAP).values

    override fun get(key: K): V? {
        return (get() ?: EMPTY_MAP)[key]
    }

    private class EmptyObservableMap<K, V> : AbstractMutableMap<K, V>(), ObservableMap<K, V> {

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() = mutableSetOf()

        override fun put(key: K, value: V): V? {
            // no-op
            return null
        }

        override fun setAll(vararg pairs: Pair<K, V>) {
            // no-op
        }

        override fun setAll(map: Map<out K, V>) {
            // no-op
        }

        override fun addListener(listener: InvalidationListener) {
            // no-op
        }

        override fun removeListener(listener: InvalidationListener) {
            // no-op
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            // no-op
            return false
        }

        override fun addListener(listener: MapChangeListener<in K, in V>) {
            // no-op
        }

        override fun removeListener(listener: MapChangeListener<in K, in V>) {
            // no-op
        }

        override fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean {
            // no-op
            return false
        }

    }

}