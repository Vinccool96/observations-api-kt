package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.Observable

/**
 * A set that allows observers to track changes when they occur.
 *
 * @param E
 *         the type of elements maintained by this set
 *
 * @see SetChangeListener
 * @see SetChangeListener.Change
 */
interface ObservableSet<E> : MutableSet<E>, Observable {

    /**
     * Add a listener to this observable set.
     *
     * @param listener
     *         the listener for listening to the set changes
     */
    fun addListener(listener: SetChangeListener<in E>)

    /**
     * Tries to removed a listener from this observable set. If the listener is not attached to this list, nothing
     * happens.
     *
     * @param listener
     *         a listener to remove
     */
    fun removeListener(listener: SetChangeListener<in E>)

    /**
     * Verify if a `SetChangeListener` already exist for this `ObservableSet`.
     *
     * @param listener
     *         the `SetChangeListener` to verify
     *
     * @return `true`, if the listener already listens, `false` otherwise.
     */
    fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean

}


