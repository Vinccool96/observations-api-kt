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
    fun hasListener(listener: MapChangeListener<in K, in V>): Boolean

    /**
     * Clears the ObservableMap and adds all the pairs passed as var-args.
     *
     * @param pairs the pairs to set
     */
    fun setAll(vararg pairs: Pair<K, V>)

    /**
     * Clears the ObservableMap and adds all pairs from the map.
     *
     * @param map the map with pairs that will be added to this ObservableMap
     */
    fun setAll(map: Map<out K, V>)

}