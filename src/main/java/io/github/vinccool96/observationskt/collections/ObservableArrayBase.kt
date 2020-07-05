package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.sun.collections.ArrayListenerHelper

/**
 * Abstract class that serves as a base class for [ObservableArray] implementations. The base class provides listener
 * handling functionality by implementing `addListener` and `removeListener` methods. [fireChange] method is provided
 * for notifying the listeners.
 *
 * @param T
 *         actual array instance type
 *
 * @see ObservableArray
 * @see ArrayChangeListener
 * @since JavaFX 8.0
 */
@Suppress("UNCHECKED_CAST")
abstract class ObservableArrayBase<T : ObservableArray<T>> : ObservableArray<T> {

    private var helper: ArrayListenerHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayListenerHelper.addListener(this.helper, this as T, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<T>) {
        if (!isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayListenerHelper.addListener(this.helper, this as T, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<T>) {
        if (isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayListenerHelper.removeListener(this.helper, listener)
        }
    }

    override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    /**
     * Notifies all listeners of a change
     *
     * @param sizeChanged
     *         if the size changed
     * @param from
     *         index of the change start
     * @param to
     *         index of the change to
     */
    protected fun fireChange(sizeChanged: Boolean, from: Int, to: Int) {
        ArrayListenerHelper.fireValueChangedEvent(this.helper, sizeChanged, from, to)
    }

}