package io.github.vinccool96.observationskt.sun.collections

/**
 * SortableList is a list that can sort itself in an efficient way, in contrast to the Collections.sort() method which
 * threat all lists the same way. E.g. ObservableList can sort and fire only one notification.
 *
 * @param E the type of elements in this list
 */
interface SortableList<E> : MutableList<E> {

    /**
     * Sort using default comparator
     *
     * @throws ClassCastException if some of the elements cannot be cast to Comparable
     * @throws UnsupportedOperationException if list's iterator doesn't support set
     */
    fun sort()

    /**
     * Sort using comparator
     *
     * @param comparator the comparator to use
     *
     * @throws ClassCastException if the list contains elements that are not *mutually comparable* using the specified
     * comparator.
     * @throws UnsupportedOperationException if the specified list's list-iterator does not support the `set` operation.
     */
    fun sort(comparator: Comparator<in E>)

}