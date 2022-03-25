package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ArrayChangeListener
import io.github.vinccool96.observationskt.collections.ObservableArray
import io.github.vinccool96.observationskt.collections.ObservableCollections
import io.github.vinccool96.observationskt.sun.binding.ArrayExpressionHelper

@Suppress("PrivatePropertyName")
class ObservableArrayValueStub<T>(value: ObservableArray<T>?, baseArrayOfNull: Array<T>) : ObservableArrayValue<T> {

    private var valueState: ObservableArray<T>? = value

    private val EMPTY_ARRAY: ObservableArray<T> = ObservableCollections.emptyObservableArray(baseArrayOfNull)

    private var helper: ArrayExpressionHelper<T>? = null

    constructor(baseArrayOfNull: Array<T>) : this(null, baseArrayOfNull)

    override val value: ObservableArray<T>?
        get() = this.valueState

    override fun get(): ObservableArray<T>? {
        return this.valueState
    }

    fun set(value: ObservableArray<T>?) {
        this.valueState = value
        this.fireValueChangedEvent()
    }

    override fun addListener(listener: InvalidationListener) {
        if (!isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (isInvalidationListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (!isChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ChangeListener<in ObservableArray<T>?>) {
        if (isChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isChangeListenerAlreadyAdded(listener: ChangeListener<in ObservableArray<T>?>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.changeListeners.contains(listener)
    }

    override fun addListener(listener: ArrayChangeListener<in T>) {
        if (!isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.addListener(this.helper, this, listener)
        }
    }

    override fun removeListener(listener: ArrayChangeListener<in T>) {
        if (isArrayChangeListenerAlreadyAdded(listener)) {
            this.helper = ArrayExpressionHelper.removeListener(this.helper, listener)
        }
    }

    override fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<in T>): Boolean {
        val curHelper = this.helper
        return curHelper != null && curHelper.arrayChangeListeners.contains(listener)
    }

    fun fireValueChangedEvent() {
        ArrayExpressionHelper.fireValueChangedEvent(this.helper)
    }

    override val size: Int
        get() = (this.get() ?: EMPTY_ARRAY).size

    override fun clear() {
        (this.get() ?: EMPTY_ARRAY).clear()
    }

    override fun isEmpty(): Boolean {
        return (this.get() ?: EMPTY_ARRAY).isEmpty()
    }

    override operator fun get(index: Int): T {
        return (this.get() ?: EMPTY_ARRAY)[index]
    }

    override operator fun set(index: Int, value: T) {
        (this.get() ?: EMPTY_ARRAY)[index] = value
    }

    override fun addAll(vararg elements: T) {
        (this.get() ?: EMPTY_ARRAY).addAll(*elements)
    }

    override fun addAll(src: ObservableArray<T>) {
        (this.get() ?: EMPTY_ARRAY).addAll(src)
    }

    override fun addAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).addAll(src, startIndex, endIndex)
    }

    override fun addAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).addAll(src, startIndex, endIndex)
    }

    override fun setAll(vararg elements: T) {
        (this.get() ?: EMPTY_ARRAY).setAll(*elements)
    }

    override fun setAll(src: ObservableArray<T>) {
        (this.get() ?: EMPTY_ARRAY).setAll(src)
    }

    override fun setAll(src: Array<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).setAll(src, startIndex, endIndex)
    }

    override fun setAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).setAll(src, startIndex, endIndex)
    }

    override fun set(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).set(src, destinationOffset, startIndex, endIndex)
    }

    override fun set(src: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).set(src, destinationOffset, startIndex, endIndex)
    }

    override fun contains(element: T): Boolean {
        return (this.get() ?: EMPTY_ARRAY).contains(element)
    }

    override fun containsAll(elements: ObservableArray<T>): Boolean {
        return (this.get() ?: EMPTY_ARRAY).containsAll(elements)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return (this.get() ?: EMPTY_ARRAY).containsAll(elements)
    }

    override fun containsAll(vararg elements: T): Boolean {
        return (this.get() ?: EMPTY_ARRAY).containsAll(*elements)
    }

    override fun trimToSize() {
        (this.get() ?: EMPTY_ARRAY).trimToSize()
    }

    override fun resize(size: Int) {
        (this.get() ?: EMPTY_ARRAY).resize(size)
    }

    override fun ensureCapacity(capacity: Int) {
        (this.get() ?: EMPTY_ARRAY).ensureCapacity(capacity)
    }

    override fun copyInto(destination: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override fun copyInto(destination: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        (this.get() ?: EMPTY_ARRAY).copyInto(destination, destinationOffset, startIndex, endIndex)
    }

    override val baseArray: Array<T>
        get() = (this.get() ?: EMPTY_ARRAY).baseArray

    override operator fun iterator(): Iterator<T> {
        return (this.get() ?: EMPTY_ARRAY).iterator()
    }

    override fun toTypedArray(): Array<T> {
        return (this.get() ?: EMPTY_ARRAY).toTypedArray()
    }

    override fun toTypedArray(startIndex: Int, endIndex: Int): Array<T> {
        return (this.get() ?: EMPTY_ARRAY).toTypedArray(startIndex, endIndex)
    }
}