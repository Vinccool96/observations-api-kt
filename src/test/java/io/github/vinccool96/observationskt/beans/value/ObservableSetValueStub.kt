package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.sun.binding.SetExpressionHelper

@Suppress("PrivatePropertyName")
class ObservableSetValueStub<E>(value: ObservableSet<E>?) : ObservableSetValue<E> {

    private val EMPTY_SET: ObservableSet<E> = ObservableCollections.emptyObservableSet()

    private var valueState = value

    private var helper: SetExpressionHelper<E>? = null

    constructor() : this(null)

    override fun get(): ObservableSet<E>? {
        return this.valueState
    }

    fun set(value: ObservableSet<E>?) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override val value: ObservableSet<E>?
        get() = this.get()

    override val size: Int
        get() = (this.get() ?: EMPTY_SET).size

    override fun isEmpty(): Boolean {
        return (this.get() ?: EMPTY_SET).isEmpty()
    }

    override fun contains(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).contains(element)
    }

    override fun iterator(): MutableIterator<E> {
        return (this.get() ?: EMPTY_SET).iterator()
    }

    override fun add(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).add(element)
    }

    override fun remove(element: E): Boolean {
        return (this.get() ?: EMPTY_SET).remove(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).containsAll(elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).addAll(elements)
    }

    override fun setAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_SET).setAll(*elements)
    }

    override fun setAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).setAll(elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_SET).retainAll(elements)
    }

    override fun clear() {
        (this.get() ?: EMPTY_SET).clear()
    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableSet<E>?>) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: ChangeListener<in ObservableSet<E>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: SetChangeListener<in E>) {
        if (!hasListener(listener)) {
            this.helper = SetExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: SetChangeListener<in E>) {
        if (hasListener(listener)) {
            this.helper = SetExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun hasListener(listener: SetChangeListener<in E>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.setChangeListeners.contains(listener)
    }

    fun fireValueChangedEvent() {
        SetExpressionHelper.fireValueChangedEvent(this.helper)
    }

}