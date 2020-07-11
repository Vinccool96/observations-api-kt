package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.Observable

/**
 * A map that allows observers to track changes when they occur.
 *
 * @param K the type of keys maintained by this map
 * @param V the type of mapped values
 *
 * @see MapChangeListener
 * @see MapChangeListener.Change
 */
interface ObservableMap<K, V> : MutableMap<K, V>, Observable {

    /**
     * Add a listener to this observable map.
     *
     * @param listener the listener for listening to the list changes
     */
    fun addListener(listener: MapChangeListener<in K, in V>)

    /**
     * Tries to removed a listener from this observable map. If the listener is not attached to this map, nothing
     * happens.
     *
     * @param listener a listener to remove
     */
    fun removeListener(listener: MapChangeListener<in K, in V>)

    /**
     * Verify if a `MapChangeListener` already exist for this `ObservableMap`.
     *
     * @param listener the `MapChangeListener` to verify
     *
     * @return `true`, if the listener already listens, `false` otherwise.
     */
    fun isMapChangeListenerAlreadyAdded(listener: MapChangeListener<in K, in V>): Boolean
}