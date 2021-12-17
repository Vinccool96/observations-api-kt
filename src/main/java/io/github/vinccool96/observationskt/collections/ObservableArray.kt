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
 * @param T the type of the `Array` elements
 *
 * @see ArrayChangeListener
 */
interface ObservableArray<T> : Observable {

    /**
     * The base array that needs to be provided to fill the array when resizing. It must be of size `1`, and the value
     * it contains is the base value.
     */
    val baseArray: Array<T>

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

    /**
     * Copies specified portion of array into `dest` array. Throws the same exceptions as [Array.copyInto] method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: Array<T>, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = this.size)

    /**
     * Copies specified portion of array into `dest` observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: ObservableArray<T>, destinationOffset: Int = 0, startIndex: Int = 0,
            endIndex: Int = this.size)

    /**
     * Gets a single value of array. This is generally as fast as direct access to an array and eliminates necessity to
     * make a copy of array.
     *
     * @param index index of element to get
     *
     * @return value at the given index
     *
     * @throws ArrayIndexOutOfBoundsException if `index` is outside array bounds
     */
    operator fun get(index: Int): T

    /**
     * Appends content of a given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param elements elements to append
     */
    fun addAll(vararg elements: T)

    /**
     * Appends content of a given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src observable array with elements to append
     */
    fun addAll(src: ObservableArray<T>)

    /**
     * Appends a portion of given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: Array<T>, startIndex: Int, endIndex: Int)

    /**
     * Appends a portion of given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int)

    /**
     * Utility function that uses [addAll]
     *
     * @param src array with elements to append
     */
    operator fun plusAssign(src: Array<T>) {
        addAll(*src)
    }

    /**
     * Utility function that uses [addAll]
     *
     * @param src observable array with elements to append
     */
    operator fun plusAssign(src: ObservableArray<T>) {
        addAll(src)
    }

    /**
     * Replaces this observable array content with given elements. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param elements elements to put into array content
     */
    fun setAll(vararg elements: T)

    /**
     * Replaces this observable array content with a copy of given observable array. Capacity is increased if necessary
     * to match the new size of the data.
     *
     * @param src source observable array to copy.
     */
    fun setAll(src: ObservableArray<T>)

    /**
     * Replaces this observable array content with a copy of portion of a given array. Capacity is increased if
     * necessary to match the new size of the data.
     *
     * @param src source array to copy.
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: Array<T>, startIndex: Int, endIndex: Int)

    /**
     * Replaces this observable array content with a copy of portion of a given observable array. Capacity is increased
     * if necessary to match the new size of the data.
     *
     * @param src source observable array to copy.
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: ObservableArray<T>, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified array into this observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: Array<T>, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified observable array into this observable array. Throws the same exceptions as
     * [Array.copyInto] method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: ObservableArray<T>, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Sets a single value in the array. Avoid using this method if many values are updated, use [set] update method
     * instead with as minimum number of invocations as possible.
     *
     * @param index index of the value to set
     * @param value new value for the given index
     *
     * @throws ArrayIndexOutOfBoundsException if `index` is outside array bounds
     */
    operator fun set(index: Int, value: T)

    /**
     * Returns an array containing copy of the observable array.
     *
     * @return a double array containing the copy of the observable array
     */
    fun toTypedArray(): Array<T>

    /**
     * Returns an array containing copy of specified portion of the observable array.
     *
     * @param startIndex starting position in this array
     * @param endIndex the end (exclusive) of the subrange to copy
     *
     * @return a double array containing the copy of specified portion the observable array
     */
    fun toTypedArray(startIndex: Int, endIndex: Int): Array<T>

    /**
     * Creates an [Iterator] for iterating over the elements of the array. If the `ObservableArray` linked to the
     * iterator gets modified, it won't affect the `Iterator`.
     */
    operator fun iterator(): Iterator<T>

}