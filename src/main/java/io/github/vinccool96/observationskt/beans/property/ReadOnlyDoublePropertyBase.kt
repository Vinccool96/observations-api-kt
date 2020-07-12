package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.sun.binding.ExpressionHelper

/**
 * Base class for all readonly properties wrapping a `Double`. This class provides a default implementation to attach
 * listener.
 *
 * @see ReadOnlyDoubleProperty
 */
abstract class ReadOnlyDoublePropertyBase : ReadOnlyDoubleProperty() {

    private var helper: ExpressionHelper<Number?>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in Number?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in Number?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in Number?>): Boolean {
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