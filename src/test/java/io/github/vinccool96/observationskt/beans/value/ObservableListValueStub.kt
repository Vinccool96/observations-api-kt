package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.sun.binding.ListExpressionHelper

@Suppress("PrivatePropertyName")
class ObservableListValueStub<E>(value: ObservableList<E>?) : ObservableListValue<E> {

    private var valueState: ObservableList<E>? = value

    private val EMPTY_LIST: ObservableList<E> = ObservableCollections.emptyObservableList()

    private var helper: ListExpressionHelper<E>? = null

    constructor() : this(null)

    override fun get(): ObservableList<E>? {
        return this.valueState
    }

    fun set(value: ObservableList<E>?) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override val value: ObservableList<E>?
        get() = this.valueState

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

    fun fireValueChangedEvent() {
        ListExpressionHelper.fireValueChangedEvent(this.helper)
    }

    override val size: Int
        get() = (this.get() ?: EMPTY_LIST).size

    override fun isEmpty(): Boolean {
        return (this.get() ?: EMPTY_LIST).isEmpty()
    }

    override operator fun contains(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).contains(element)
    }

    override fun iterator(): MutableIterator<E> {
        return (this.get() ?: EMPTY_LIST).iterator()
    }

    override fun add(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).add(element)
    }

    override fun remove(element: E): Boolean {
        return (this.get() ?: EMPTY_LIST).remove(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).containsAll(elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(index, elements)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).retainAll(elements)
    }

    override fun clear() {
        (this.get() ?: EMPTY_LIST).clear()
    }

    override fun get(index: Int): E {
        return (this.get() ?: EMPTY_LIST)[index]
    }

    override fun set(index: Int, element: E): E {
        return (this.get() ?: EMPTY_LIST).set(index, element)
    }

    override fun add(index: Int, element: E) {
        (this.get() ?: EMPTY_LIST).add(index, element)
    }

    override fun removeAt(index: Int): E {
        return (this.get() ?: EMPTY_LIST).removeAt(index)
    }

    override fun indexOf(element: E): Int {
        return (this.get() ?: EMPTY_LIST).indexOf(element)
    }

    override fun lastIndexOf(element: E): Int {
        return (this.get() ?: EMPTY_LIST).lastIndexOf(element)
    }

    override fun listIterator(): MutableListIterator<E> {
        return (this.get() ?: EMPTY_LIST).listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<E> {
        return (this.get() ?: EMPTY_LIST).listIterator(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        return (this.get() ?: EMPTY_LIST).subList(fromIndex, toIndex)
    }

    override fun addAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).addAll(*elements)
    }

    override fun setAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).setAll(*elements)
    }

    override fun setAll(col: Collection<E>): Boolean {
        return (this.get() ?: EMPTY_LIST).setAll(col)
    }

    override fun removeAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).removeAll(*elements)
    }

    override fun retainAll(vararg elements: E): Boolean {
        return (this.get() ?: EMPTY_LIST).retainAll(*elements)
    }

    override fun remove(from: Int, to: Int) {
        (this.get() ?: EMPTY_LIST).remove(from, to)
    }

}