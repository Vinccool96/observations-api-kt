package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ListChangeListener
import io.github.vinccool96.observationskt.collections.ObservableList

@Suppress("UNCHECKED_CAST")
class ImmutableObservableList<E>(vararg elements: E) : AbstractMutableList<E>(), ObservableList<E> {

    private val elements: Array<E>?

    init {
        if (elements.isEmpty()) {
            this.elements = null
        } else {
            this.elements = elements.copyOf() as Array<E>
        }
    }

    override fun addListener(listener: InvalidationListener) {
        // no-op
    }

    override fun removeListener(listener: InvalidationListener) {
        // no-op
    }

    override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
        // no-op
        return false
    }

    override fun addListener(listener: ListChangeListener<in E>) {
        // no-op
    }

    override fun removeListener(listener: ListChangeListener<in E>) {
        // no-op
    }

    override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
        // no-op
        return false
    }

    override fun addAll(vararg elements: E): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setAll(vararg elements: E): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setAll(col: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(vararg elements: E): Boolean {
        throw UnsupportedOperationException()
    }

    override fun retainAll(vararg elements: E): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(from: Int, to: Int) {
        throw UnsupportedOperationException()
    }

    override fun get(index: Int): E {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }
        return this.elements!![index]
    }

    override val size: Int
        get() = if (this.elements != null) elements.size else 0

    override fun add(index: Int, element: E) {
        // no-op
    }

    override fun removeAt(index: Int): E {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: E): E {
        throw UnsupportedOperationException()
    }

}