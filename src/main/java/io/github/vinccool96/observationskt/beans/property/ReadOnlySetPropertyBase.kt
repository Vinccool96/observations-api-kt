package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import io.github.vinccool96.observationskt.sun.binding.SetExpressionHelper

/**
 * Base class for all readonly properties wrapping an [ObservableSet]. This class provides a default implementation to
 * attach listener.
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see ReadOnlySetProperty
 */
abstract class ReadOnlySetPropertyBase<E> : ReadOnlySetProperty<E>() {

    private var helper: SetExpressionHelper<E>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableSet<E>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: SetChangeListener<in E>) {
        if (!isSetChangeListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: SetChangeListener<in E>) {
        if (isSetChangeListenerAlreadyAdded(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isSetChangeListenerAlreadyAdded(listener: SetChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.setChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableSet] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [SetChangeListeners][SetChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        SetExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableSet] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [SetChangeListeners][SetChangeListener].
     *
     * This method is called when the content of the set changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out E>) {
        SetExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

}