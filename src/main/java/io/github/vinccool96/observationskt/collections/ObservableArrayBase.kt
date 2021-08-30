package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.sun.collections.ArrayListenerHelper

/**
 * Abstract class that serves as a base class for [ObservableArray] implementations. The base class provides listener
 * handling functionality by implementing `addListener` and `removeListener` methods. [fireChange] method is provided
 * for notifying the listeners.
 *
 * @param T actual array instance type
 *
 * @see ObservableArray
 * @see ArrayChangeListener
 */
@Suppress("UNCHECKED_CAST")
abstract class ObservableArrayBase<T> : ObservableArray<T> {

    private var helper: ArrayListenerHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayListenerHelper.addListener(this.helper, this as ObservableArray<T>, listener)
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
            this.helper = ArrayListenerHelper.addListener(this.helper, this as ObservableArray<T>, listener)
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
     * @param sizeChanged if the size changed
     * @param from index of the change start
     * @param to index of the change to
     */
    protected fun fireChange(sizeChanged: Boolean, from: Int, to: Int) {
        ArrayListenerHelper.fireValueChangedEvent(this.helper, sizeChanged, from, to)
    }

    override fun clear() {
        resize(0)
    }

    override fun addAll(vararg elements: T) {
        addAllInternal(elements as Array<T>, 0, elements.size)
    }

    override fun addAll(src: ObservableArray<T>) {
        addAll(*src.toTypedArray())
    }

    override fun addAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        addAllInternal(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        addAll(src.toTypedArray(), startIndex, endIndex)
    }

    override fun setAll(vararg elements: T) {
        setAllInternal(elements as Array<T>, 0, elements.size)
    }

    override fun setAll(src: ObservableArray<T>) {
        setAllInternal(src, 0, src.size)
    }

    override fun setAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src, startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        rangeCheck(src.toTypedArray(), startIndex, endIndex)
        setAllInternal(src, startIndex, endIndex)
    }

    override fun set(src: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toTypedArray(), destinationOffset, startIndex, endIndex)
    }

    protected abstract fun growCapacity(length: Int)

    protected abstract fun addAllInternal(src: Array<T>, startIndex: Int, endIndex: Int)

    protected abstract fun setAllInternal(src: Array<T>, startIndex: Int, endIndex: Int)

    protected abstract fun setAllInternal(src: ObservableArray<T>, startIndex: Int, endIndex: Int)

    protected fun rangeCheck(size: Int) {
        if (size > this.size) {
            throw ArrayIndexOutOfBoundsException(this.size)
        }
    }

    protected fun rangeCheck(src: Array<T>, startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex > src.size) {
            throw ArrayIndexOutOfBoundsException(src.size)
        }
        if (endIndex < startIndex) {
            throw ArrayIndexOutOfBoundsException(endIndex)
        }
    }

}