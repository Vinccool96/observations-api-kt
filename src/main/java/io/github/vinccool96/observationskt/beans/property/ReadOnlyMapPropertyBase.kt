package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener
import io.github.vinccool96.observationskt.collections.MapChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.sun.binding.MapExpressionHelper

/**
 * Base class for all readonly properties wrapping an {@link ObservableMap}. This class provides a default
 * implementation to attach listener.
 *
 * @param K the type of the key elements of the map
 * @param V the type of the value elements of the map
 *
 * @see ReadOnlyMapProperty
 */
abstract class ReadOnlyMapPropertyBase<K, V> : ReadOnlyMapProperty<K, V>() {

    private var helper: MapExpressionHelper<K, V>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableMap<K, V>?>) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableMap<K, V>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: MapChangeListener<in K, in V>) {
        if (!hasListener(listener)) {
            this.helper = MapExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: MapChangeListener<in K, in V>) {
        if (hasListener(listener)) {
            this.helper = MapExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: MapChangeListener<in K, in V>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.mapChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableMap] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [MapChangeListeners][MapChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        MapExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableMap] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [MapChangeListeners][MapChangeListener].
     *
     * This method is called when the content of the list changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out K, out V>) {
        MapExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

}