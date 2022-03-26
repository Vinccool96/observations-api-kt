package io.github.vinccool96.observationskt.sun.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.collections.ObservableSet
import io.github.vinccool96.observationskt.collections.SetChangeListener
import io.github.vinccool96.observationskt.collections.SetChangeListener.Change
import java.util.*

/**
 * A Set wrapper class that implements observability.
 *
 * @constructor Creates new instance of ObservableSet that wraps the particular set specified by the parameter set.
 *
 * @param set the set being wrapped
 */
open class ObservableSetWrapper<E>(set: MutableSet<E>) : ObservableSet<E> {

    private val backingSet: MutableSet<E> = set

    private var listenerHelper: SetListenerHelper<E>? = null

    private inner class SimpleAddChange(private val added: E) : Change<E>(this) {

        override val wasAdded: Boolean = true

        override val wasRemoved: Boolean = false

        override val elementAdded: E?
            get() = this.added

        override val elementRemoved: E? = null

        override fun toString(): String {
            return "added $added"
        }

    }

    private inner class SimpleRemoveChange(private val removed: E) : Change<E>(this) {

        override val wasAdded: Boolean = false

        override val wasRemoved: Boolean = true

        override val elementAdded: E? = null

        override val elementRemoved: E?
            get() = this.removed

        override fun toString(): String {
            return "removed $removed"
        }

    }

    private fun callObservers(change: Change<E>) {
        SetListenerHelper.fireValueChangedEvent(this.listenerHelper, change)
    }

    override fun addListener(listener: InvalidationListener) {
        if (!hasListener(listener)) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
        }
    }

    override fun removeListener(listener: InvalidationListener) {
        if (hasListener(listener)) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
        }
    }

    override fun hasListener(listener: InvalidationListener): Boolean {
        val curHelper = this.listenerHelper
        return curHelper != null && curHelper.invalidationListeners.contains(listener)
    }

    override fun addListener(listener: SetChangeListener<in E>) {
        if (!hasListener(listener)) {
            this.listenerHelper = SetListenerHelper.addListener(this.listenerHelper, listener)
        }
    }

    override fun removeListener(listener: SetChangeListener<in E>) {
        if (hasListener(listener)) {
            this.listenerHelper = SetListenerHelper.removeListener(this.listenerHelper, listener)
        }
    }

    override fun hasListener(listener: SetChangeListener<in E>): Boolean {
        val curHelper = this.listenerHelper
        return curHelper != null && curHelper.setChangeListeners.contains(listener)
    }

    /**
     * Returns number of elements contained in this set.
     *
     * @return number of elements contained in the set
     *
     * @see MutableSet in Kotlin API documentation
     */
    override val size: Int
        get() = this.backingSet.size

    /**
     * Returns true if this set contains no elements.
     *
     * @return true if this set contains no elements
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun isEmpty(): Boolean {
        return this.backingSet.isEmpty()
    }

    /**
     * Returns true if this set contains specified element.
     *
     * @param element an element that is being looked for
     *
     * @return true if this set contains specified element
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun contains(element: E): Boolean {
        return this.backingSet.contains(element)
    }

    /**
     * Returns an iterator over the elements in this set. If the {@code remove()} of the iterator method is called then
     * the registered observers are called as well.
     *
     * @return an iterator over the elements in this set
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun iterator(): MutableIterator<E> {
        return object : MutableIterator<E> {

            private val backingIt: MutableIterator<E> = this@ObservableSetWrapper.backingSet.iterator()

            private lateinit var lastElement: Holder<E>

            override fun hasNext(): Boolean {
                return this.backingIt.hasNext()
            }

            override fun next(): E {
                this.lastElement = Holder(this.backingIt.next())
                return this.lastElement.element
            }

            override fun remove() {
                this.backingIt.remove()
                callObservers(SimpleRemoveChange(this.lastElement.element))
            }

        }
    }

    /**
     * Adds the specific element into this set and call all the registered observers unless the set already contains the
     * element. Returns true in the case the element was added to the set.
     *
     * @param element the element to be added to the set
     *
     * @return true if the element was added
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun add(element: E): Boolean {
        val ret = this.backingSet.add(element)
        if (ret) {
            callObservers(SimpleAddChange(element))
        }
        return ret
    }

    /**
     * Removes the specific element from this set and call all the registered observers if the set contained the
     * element. Returns true in the case the element was removed from the set.
     *
     * @param element the element to be removed from the set
     *
     * @return true if the element was removed
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun remove(element: E): Boolean {
        val ret = this.backingSet.remove(element)
        if (ret) {
            callObservers(SimpleRemoveChange(element))
        }
        return ret
    }

    /**
     * Test this set if it contains all the elements in the specified collection. In such case returns true.
     *
     * @param elements collection to be checked for containment in this set
     *
     * @return true if the set contains all the elements in the specified collection
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun containsAll(elements: Collection<E>): Boolean {
        return this.backingSet.containsAll(elements)
    }

    /**
     * Adds the elements from the specified collection. Observers are called for each elements that was not already
     * present in the set.
     *
     * @param elements collection containing elements to be added to this set
     *
     * @return true if this set changed as a result of the call
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun addAll(elements: Collection<E>): Boolean {
        var ret = false
        for (element in elements) {
            ret = add(element) || ret
        }
        return ret
    }

    override fun setAll(vararg elements: E): Boolean {
        val toRemove = LinkedList<E>()
        for (e in backingSet) {
            if (e !in elements) {
                toRemove.add(e)
            }
        }

        for (e in toRemove) {
            remove(e)
        }

        for (element in elements) {
            add(element)
        }

        return true
    }

    override fun setAll(elements: Collection<E>): Boolean {
        val toRemove = LinkedList<E>()
        for (e in backingSet) {
            if (e !in elements) {
                toRemove.add(e)
            }
        }

        for (e in toRemove) {
            remove(e)
        }

        for (element in elements) {
            add(element)
        }

        return true
    }

    /**
     * Keeps only elements that are included the specified collection. All other elements are removed. For each removed
     * element all the observers are called.
     *
     * @param elements collection containing elements to be kept in this set
     *
     * @return true if this set changed as a result of the call
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun retainAll(elements: Collection<E>): Boolean {
        return removeRetain(elements, false)
    }

    /**
     * Removes all the elements that are contained in the specified collection. Observers are called for each removed
     * element.
     *
     * @param elements collection containing elements to be removed from this set
     *
     * @return true if this set changed as a result of the call
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun removeAll(elements: Collection<E>): Boolean {
        return removeRetain(elements, true)
    }

    private fun removeRetain(elements: Collection<E>, remove: Boolean): Boolean {
        var removed = false
        val i = this.backingSet.iterator()
        while (i.hasNext()) {
            val element = i.next()
            if (remove == elements.contains(element)) {
                removed = true
                i.remove()
                callObservers(SimpleRemoveChange(element))
            }
        }
        return removed
    }

    /**
     * Removes all the elements from this set. Observers are called for each element.
     *
     * @see MutableSet in Kotlin API documentation
     */
    override fun clear() {
        val i = this.backingSet.iterator()
        while (i.hasNext()) {
            val element = i.next()
            i.remove()
            callObservers(SimpleRemoveChange(element))
        }
    }

    /**
     * Returns the String representation of the wrapped set.
     *
     * @return the String representation of the wrapped set
     *
     * @see Any in Kotlin API documentation
     */
    override fun toString(): String {
        return this.backingSet.toString()
    }

    /**
     * Indicates whether some other object is "equal to" the wrapped set.
     *
     * @param other the reference object with which to compare
     *
     * @return true if the wrapped is equal to the obj argument
     *
     * @see Any in Kotlin API documentation
     */
    override fun equals(other: Any?): Boolean {
        return this.backingSet == other
    }

    /**
     * Returns the hash code for the wrapped set.
     *
     * @return the hash code for the wrapped set
     *
     * @see Any in Kotlin API documentation
     */
    override fun hashCode(): Int {
        return this.backingSet.hashCode()
    }

    private class Holder<E>(val element: E)

}