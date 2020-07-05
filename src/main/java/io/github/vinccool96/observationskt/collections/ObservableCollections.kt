package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.InvalidationListener
import io.github.vinccool96.observationskt.beans.Observable
import io.github.vinccool96.observationskt.collections.ObservableCollections.emptyObservableList
import io.github.vinccool96.observationskt.sun.collections.*
import io.github.vinccool96.observationskt.util.Callback
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

/**
 * Utility object that consists of methods that are 1:1 copies of [Collections] methods.
 *
 * The wrapper methods (like [synchronizedObservableList] or [emptyObservableList]) has exactly the same functionality
 * as the methods in `Collections`, with exception that they return [ObservableList] and are therefore suitable for
 * methods that require `ObservableList` on input.
 *
 * The utility methods are here mainly for performance reasons. All methods are optimized in a way that they yield only
 * limited number of notifications. On the other hand, `Collections` methods might call "modification methods" on an
 * `ObservableList` multiple times, resulting in a number of notifications.
 *
 * @since JavaFX 2.0
 */
object ObservableCollections {

    /**
     * Constructs an ObservableList that is backed by the specified list. Mutation operations on the ObservableList
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying list are *not* reported to observers of any
     * ObservableList that wraps it.
     *
     * @param E
     *         the list element type
     * @param list
     *         a concrete List that backs this ObservableList
     *
     * @return a newly created ObservableList
     */
    fun <E> observableList(list: MutableList<E>): ObservableList<E> {
        return if (list is RandomAccess) ObservableListWrapper(list) else ObservableSequentialListWrapper(list)
    }

    /**
     * Constructs an ObservableList that is backed by the specified list. Mutation operations on the ObservableList
     * instance will be reported to observers that have registered on that instance.
     *
     * Note that mutation operations made directly to the underlying list are *not* reported to observers of any
     * ObservableList that wraps it.
     *
     * This list also reports mutations of the elements in it by using `extractor`. Observable objects returned by
     * `extractor` (applied to each list element) are listened for changes and transformed into "update" change of
     * ListChangeListener.
     *
     * @param E
     *         the list element type
     * @param list
     *         a concrete List that backs this ObservableList
     * @param extractor
     *         element to Observable[] convertor
     *
     * @return a newly created ObservableList
     *
     * @since JavaFX 2.1
     */
    fun <E> observableList(list: MutableList<E>, extractor: Callback<E, Array<Observable>>): ObservableList<E> {
        return if (list is RandomAccess) ObservableListWrapper(list, extractor)
        else ObservableSequentialListWrapper(list, extractor)
    }

    /**
     * Creates a new empty observable list that is backed by an arraylist.
     *
     * @param E
     *         the list element type
     *
     * @return a newly created ObservableList
     *
     * @see observableList
     */
    fun <E> observableArrayList(): ObservableList<E> {
        return observableList(ArrayList())
    }

    /**
     * Creates a new empty observable list backed by an arraylist.
     *
     * This list reports element updates.
     *
     * @param E
     *         the list element type
     * @param extractor
     *         element to Observable[] convertor. Observable objects are listened for changes on the element.
     *
     * @return a newly created ObservableList
     *
     * @see observableList
     * @since JavaFX 2.1
     */
    fun <E> observableArrayList(extractor: Callback<E, Array<Observable>>): ObservableList<E> {
        return observableList(ArrayList(), extractor)
    }

    /**
     * Creates a new observable array list with `items` added to it.
     *
     * @param E
     *         the list element type
     * @param items
     *         the items that will be in the new observable ArrayList
     *
     * @return a newly created observableArrayList
     *
     * @see observableArrayList
     */
    fun <E> observableArrayList(vararg items: E): ObservableList<E> {
        val list: ObservableList<E> = observableArrayList()
        list.addAll(*items)
        return list
    }

    /**
     * Creates a new observable array list and adds a content of collection `col` to it.
     *
     * @param E
     *         the list element type
     * @param col
     *         a collection which content should be added to the observableArrayList
     *
     * @return a newly created observableArrayList
     */
    fun <E> observableArrayList(col: Collection<E>): ObservableList<E> {
        val list: ObservableList<E> = observableArrayList()
        list.addAll(col)
        return list
    }

    /**
     * Creates and returns unmodifiable wrapper list on top of provided observable list.
     *
     * @param E
     *         the list element type
     * @param list
     *         an ObservableList that is to be wrapped
     *
     * @return an ObservableList wrapper that is unmodifiable
     *
     * @see Collections.unmodifiableList
     */
    fun <E> unmodifiableObservableList(list: ObservableList<E>): ObservableList<E> {
        return UnmodifiableObservableListImpl(list)
    }

    /**
     * Creates and empty unmodifiable observable list.
     *
     * @param E the list element type
     *
     * @return An empty unmodifiable observable list
     *
     * @see Collections.emptyList
     */
    @ReturnsUnmodifiableCollection
    fun <E> emptyObservableList(): ObservableList<E> {
        return EmptyObservableList()
    }

    /**
     * Creates an unmodifiable observable list with single element.
     *
     * @param E the list element type
     * @param e the only elements that will be contained in this singleton observable list
     *
     * @return a singleton observable list
     *
     * @see Collections.singletonList
     */
    @ReturnsUnmodifiableCollection
    fun <E> singletonObservableList(e: E): ObservableList<E> {
        return SingletonObservableList(e)
    }

    /**
     * Sorts the provided observable list. Fires only **one** change notification on the list.
     *
     * @param T
     *         the list element type
     * @param list
     *         the list to be sorted
     *
     * @see Collections#sort(List)
     */
    fun <T : Comparable<T>> sort(list: ObservableList<T>) {
        if (list is SortableList<*>) {
            list.sort()
        } else {
            val newContent: MutableList<T> = ArrayList(list)
            newContent.sort()
            list.setAll(newContent)
        }
    }

    /**
     * Sorts the provided observable list using the c comparator. Fires only **one** change notification on the list.
     *
     * @param T
     *         the list element type
     * @param list
     *         the list to sort
     * @param c
     *         comparator used for sorting. Null if natural ordering is required.
     *
     * @see Collections.sort
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> sort(list: ObservableList<T>, c: Comparator<in T>) {
        if (list is SortableList<*>) {
            (list as SortableList<T>).sortWith(c)
        } else {
            val newContent: MutableList<T> = ArrayList(list)
            newContent.sortWith(c)
            list.setAll(newContent)
        }
    }

    private class EmptyObservableList<E> : AbstractMutableList<E>(), ObservableList<E> {

        private val iterator: MutableListIterator<E>

        init {
            this.iterator = object : MutableListIterator<E> {

                override fun hasNext(): Boolean {
                    return false
                }

                override fun next(): E {
                    throw NoSuchElementException()
                }

                override fun remove() {
                    throw UnsupportedOperationException()
                }

                override fun hasPrevious(): Boolean {
                    return false
                }

                override fun previous(): E {
                    throw NoSuchElementException()
                }

                override fun nextIndex(): Int {
                    return 0
                }

                override fun previousIndex(): Int {
                    return -1
                }

                override fun set(element: E) {
                    throw UnsupportedOperationException()
                }

                override fun add(element: E) {
                    throw UnsupportedOperationException()
                }

            }
        }

        override fun add(index: Int, element: E) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): E {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: E): E {
            throw UnsupportedOperationException()
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

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ListChangeListener<in E>) {
        }

        override fun removeListener(listener: ListChangeListener<in E>) {
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 0

        override fun contains(element: E): Boolean {
            return false
        }

        override fun iterator(): MutableIterator<E> {
            return this.iterator
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return elements.isEmpty()
        }

        override fun get(index: Int): E {
            throw IndexOutOfBoundsException()
        }

        override fun indexOf(element: E): Int {
            return -1
        }

        override fun lastIndexOf(element: E): Int {
            return -1
        }

        override fun listIterator(): MutableListIterator<E> {
            return this.iterator
        }

        override fun listIterator(index: Int): MutableListIterator<E> {
            if (index != 0) {
                throw IndexOutOfBoundsException()
            }
            return this.iterator
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
            if (fromIndex != 0 || toIndex != 0) {
                throw IndexOutOfBoundsException()
            }
            return this
        }

    }

    private class SingletonObservableList<E>(private val element: E) : AbstractMutableList<E>(), ObservableList<E> {

        override fun add(index: Int, element: E) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): E {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: E): E {
            throw UnsupportedOperationException()
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

        override fun addListener(listener: InvalidationListener) {
        }

        override fun removeListener(listener: InvalidationListener) {
        }

        override fun isInvalidationListenerAlreadyAdded(listener: InvalidationListener): Boolean {
            return false
        }

        override fun addListener(listener: ListChangeListener<in E>) {
        }

        override fun removeListener(listener: ListChangeListener<in E>) {
        }

        override fun isListChangeListenerAlreadyAdded(listener: ListChangeListener<in E>): Boolean {
            return false
        }

        override val size: Int
            get() = 1

        override fun isEmpty(): Boolean {
            return false
        }

        override fun contains(element: E): Boolean {
            return this.element == element
        }

        override fun get(index: Int): E {
            if (index != 0) {
                throw IndexOutOfBoundsException()
            }
            return this.element
        }

    }

    private class UnmodifiableObservableListImpl<T>(private val backingList: ObservableList<T>) :
            ObservableListBase<T>(), ObservableList<T> {

        private val listener: ListChangeListener<T>

        init {
            this.listener = object : ListChangeListener<T> {

                override fun onChanged(change: ListChangeListener.Change<out T>) {
                    fireChange(SourceAdapterChange(this@UnmodifiableObservableListImpl, change))
                }

            }
            this.backingList.addListener(this.listener)
        }

        override fun get(index: Int): T {
            return this.backingList[index]
        }

        override val size: Int
            get() = this.backingList.size

        override fun add(index: Int, element: T) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): T {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: T): T {
            throw UnsupportedOperationException()
        }

        override fun addAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun setAll(col: Collection<T>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun removeAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun retainAll(vararg elements: T): Boolean {
            throw UnsupportedOperationException()
        }

        override fun remove(from: Int, to: Int) {
            throw UnsupportedOperationException()
        }

    }

}