package io.github.vinccool96.observationskt.collections

import io.github.vinccool96.observationskt.beans.Observable

/**
 * `ObservableArray` is an array that allows listeners to track changes when they occur. In order to track changes, the
 * internal array is encapsulated and there is no direct access available from the outside. Bulk operations are
 * supported, but they always do a copy of the data range. You can find them in subclasses as they deal with primitive
 * arrays directly.
 *
 * Implementations have both `capacity`, which is internal array length, and `size`. If size needs to be increased
 * beyond capacity, the capacity increases to match that new size. Use [trimToSize] method to shrink it.
 *
 * @param T actual array instance type
 *
 * @see ArrayChangeListener
 */
interface ObservableArray<T : ObservableArray<T>> : Observable {

    /**
     * Add a listener to this observable array.
     *
     * @param listener the listener for listening to the array changes
     */
    fun addListener(listener: ArrayChangeListener<T>)

    /**
     * Tries to remove a listener from this observable array. If the listener is not attached to this array, nothing
     * happens.
     *
     * @param listener a listener to remove
     */
    fun removeListener(listener: ArrayChangeListener<T>)

    /**
     * Verify if a `ArrayChangeListener` already exist for this `ObservableArray`.
     *
     * @param listener the `ArrayChangeListener` to verify
     *
     * @return `true`, if the listener already listens, `false` otherwise.
     */
    fun isArrayChangeListenerAlreadyAdded(listener: ArrayChangeListener<T>): Boolean

    /**
     * Sets new length of data in this array. This method grows capacity if necessary but never shrinks it. Resulting
     * array will contain existing data for indexes that are less than the current size and `false` for indexes that are
     * greater than the current size.
     *
     * @param size new length of data in this array
     *
     * @throws NegativeArraySizeException if size is negative
     */
    fun resize(size: Int)

    /**
     * Grows the capacity of this array if the current capacity is less than given `capacity`, does nothing if it
     * already exceeds the `capacity`.
     *
     * @param capacity the new capacity of the array
     */
    fun ensureCapacity(capacity: Int)

    /**
     * Shrinks the capacity to the current size of data in the array.
     */
    fun trimToSize()

    /**
     * Empties the array by resizing it to `0`. Capacity is not changed.
     *
     * @see trimToSize
     */
    fun clear()

    /**
     * Retrieves length of data in this array.
     */
    val size: Int

}