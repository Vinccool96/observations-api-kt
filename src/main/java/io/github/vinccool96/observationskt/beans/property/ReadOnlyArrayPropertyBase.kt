package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.sun.binding.ArrayExpressionHelper

/**
 * Base class for all readonly properties wrapping a [ObservableArray]. This class provides a default implementation to
 * attach listeners.
 *
 * @param T the type of the `Array` elements
 *
 * @see ReadOnlyArrayProperty
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
abstract class ReadOnlyArrayPropertyBase<T>(baseArrayOfNull: Array<T>) : ReadOnlyArrayProperty<T>(baseArrayOfNull) {

    private var helper: ArrayExpressionHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableArray<T>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<in T>) {
        if (!hasListener(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<in T>) {
        if (hasListener(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ArrayChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableArray] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ArrayChangeListeners][ArrayChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        ArrayExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableArray] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ArrayChangeListeners][ArrayChangeListener].
     *
     * This method is called when the content of the array changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out T>) {
        ArrayExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

}