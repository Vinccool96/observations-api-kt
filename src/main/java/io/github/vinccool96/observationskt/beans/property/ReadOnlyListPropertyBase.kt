package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.value.ChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ListChangeListener.Change
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.ListExpressionHelper

/**
 * Base class for all readonly properties wrapping a [ObservableList]. This class provides a default implementation to
 * attach listeners.
 *
 * @see ReadOnlyListProperty
 */
abstract class ReadOnlyListPropertyBase<E> : ReadOnlyListProperty<E>() {

    private var helper: ListExpressionHelper<E>? = null

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableList<E>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableList<E>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableList<E>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ListChangeListener<in E>) {
        if (!isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ListChangeListener<in E>) {
        if (isListChangeListenerAlreadyAdded(listener)) {
            this.helper = ListExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.listChangeListeners.contains(listener)
    }

    /**
     * This method needs to be called if the reference to the [ObservableList] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ListChangeListeners][ListChangeListener].
     *
     * This method needs to be called, if the value of this property changes.
     */
    protected open fun fireValueChangedEvent() {
        ListExpressionHelper.fireValueChangedEvent(this.helper)
    }

    /**
     * This method needs to be called if the content of the referenced [ObservableList] changes.
     *
     * It sends notifications to all attached [InvalidationListeners][InvalidationListener],
     * [ChangeListeners][ChangeListener], and [ListChangeListeners][ListChangeListener].
     *
     * This method is called when the content of the list changes.
     *
     * @param change the change that needs to be propagated
     */
    protected open fun fireValueChangedEvent(change: Change<out E>) {
        ListExpressionHelper.fireValueChangedEvent(this.helper, change)
    }

}