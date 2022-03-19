package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.collections.transformation.FilteredList
import io.github.vinccool96.observationskt.collections.transformation.SortedList
import java.text.Collator
import java.util.function.Predicate

/**
 * A list that allows listeners to track changes when they occur.
 *
 * @param E the list element type
 *
 * @see ListChangeListener
 * @see ListChangeListener.Change
 */
@Suppress("UNCHECKED_CAST")
interface ObservableList<E> : MutableList<E>, Observable {

    /**
     * Add a listener to this observable list.
     *
     * @param listener the listener for listening to the list changes
     */
    fun addListener(listener: ListChangeListener<in E>)

    /**
     * Tries to remove a listener from this observable list. If the listener is not attached to this list, nothing
     * happens.
     *
     * @param listener a listener to remove
     */
    fun removeListener(listener: ListChangeListener<in E>)

    /**
     * Verify if a `ListChangeListener` already exist for this `ObservableList`.
     *
     * @param listener the `ListChangeListener` to verify
     *
     * @return `true`, if the listener already listens, `false` otherwise.
     */
    fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean

    // Convenience methods

    /**
     * A convenient method for var-arg adding of elements.
     *
     * @param elements the elements to add
     *
     * @return `true` (as specified by [MutableCollection.add])
     */
    fun addAll(vararg elements: E): Boolean

    /**
     * Clears the ObservableList and add all the elements passed as var-args.
     *
     * @param elements the elements to set
     *
     * @return true (as specified by [MutableCollection.add])
     */
    fun setAll(vararg elements: E): Boolean

    /**
     * Clears the ObservableList and add all elements from the collection.
     *
     * @param col the collection with elements that will be added to this observableArrayList
     *
     * @return true (as specified by [MutableCollection.add])
     */
    fun setAll(col: Collection<E>): Boolean

    /**
     * A convenient method for var-arg usage of removeAll method.
     *
     * @param elements the elements to be removed
     *
     * @return true if list changed as a result of this call
     */
    fun removeAll(vararg elements: E): Boolean

    /**
     * A convenient method for var-arg usage of retain method.
     *
     * @param elements the elements to be retained
     *
     * @return true if list changed as a result of this call
     */
    fun retainAll(vararg elements: E): Boolean

    /**
     * Basically a shortcut to [subList(from, to)][subList].[clear()][clear] As this is a common operation,
     * ObservableList has this method for convenient usage.
     *
     * @param from the start of the range to remove (inclusive)
     * @param to the end of the range to remove (exclusive)
     *
     * @throws IndexOutOfBoundsException if an illegal range is provided
     */
    fun remove(from: Int, to: Int)

    /**
     * Creates a [FilteredList] wrapper of this list using the specified predicate.
     *
     * @param predicate the predicate to use
     *
     * @return new `FilteredList`
     */
    fun filtered(predicate: Predicate<E>): FilteredList<E> {
        return FilteredList(this, predicate)
    }

    /**
     * Creates a [SortedList] wrapper of this list using the specified comparator.
     *
     * @param comparator the comparator to use or null for unordered List
     *
     * @return new `SortedList`
     */
    fun sorted(comparator: Comparator<E>): SortedList<E> {
        return SortedList(this, comparator)
    }

    /**
     * Creates a [SortedList] wrapper of this list with the natural ordering.
     *
     * @return new `SortedList`
     */
    fun sorted(): SortedList<E> {
        val naturalOrder: Comparator<E> = Comparator {o1, o2 ->
            return@Comparator if (o1 == null && o2 == null) {
                0
            } else if (o1 == null) {
                -1
            } else if (o2 == null) {
                1
            } else if (o1 is Comparable<*>) {
                (o1 as Comparable<E>).compareTo(o2)
            } else {
                Collator.getInstance().compare(o1.toString(), o2.toString())
            }
        }
        return this.sorted(naturalOrder)
    }

}
