package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelper

/**
 * Base class for all readonly properties wrapping an arbitrary `Object`. This class provides a default implementation
 * to attach listener.
 *
 * @param T the type of the wrapped `Object`
 *
 * @see ReadOnlyObjectProperty
 */
abstract class ReadOnlyObjectPropertyBase<T> : ReadOnlyObjectProperty<T>() {

    private var helper: ExpressionHelper<T>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in T>) {
        if (!hasListener(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in T>) {
        if (hasListener(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    /**
     * Sends notifications to all attached [InvalidationListeners][InvalidationListener] and
     * [ChangeListeners][ChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(this.helper)
    }

}