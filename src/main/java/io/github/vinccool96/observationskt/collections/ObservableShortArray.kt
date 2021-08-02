package io.github.vinccool96.observationskt.collections

/**
 * `ObservableShortArray` is a `Short` array that allows listeners to track changes when they occur. In order to track
 * changes, the internal array is encapsulated and there is no direct access available from the outside. Bulk operations
 * are supported but they always do a copy of the data range.
 *
 * @see ArrayChangeListener
 */
interface ObservableShortArray : ObservableArray<ObservableShortArray> {

    /**
     * Copies specified portion of array into `dest` array. Throws the same exceptions as [Array.copyInto] method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: ShortArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = this.size)

    /**
     * Copies specified portion of array into `dest` array. Throws the same exceptions as [Array.copyInto] method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: Array<Short>, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = this.size)

    /**
     * Copies specified portion of array into `dest` observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: ObservableShortArray, destinationOffset: Int = 0, startIndex: Int = 0,
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
    operator fun get(index: Int): Short

    /**
     * Appends given `elements` to the end of this array. Capacity is increased if necessary to match the new size of
     * the data.
     *
     * @param elements elements to append
     */
    fun addAll(vararg elements: Short)

    /**
     * Appends content of a given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src array with elements to append
     */
    fun addAll(src: Array<Short>) {
        addAll(*src.toShortArray())
    }

    /**
     * Appends content of a given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src observable array with elements to append
     */
    fun addAll(src: ObservableShortArray)

    /**
     * Appends a portion of given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: ShortArray, startIndex: Int, endIndex: Int)

    /**
     * Appends a portion of given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: Array<Short>, startIndex: Int, endIndex: Int) {
        addAll(src.toShortArray(), startIndex, endIndex)
    }

    /**
     * Appends a portion of given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: ObservableShortArray, startIndex: Int, endIndex: Int)

    /**
     * Utility function that uses [addAll]
     */
    operator fun plusAssign(shorts: ShortArray) {
        addAll(*shorts)
    }

    /**
     * Utility function that uses [addAll]
     *
     * @param src array with elements to append
     */
    operator fun plusAssign(src: Array<Short>) {
        addAll(*src.toShortArray())
    }

    /**
     * Utility function that uses [addAll]
     */
    operator fun plusAssign(src: ObservableShortArray) {
        addAll(src)
    }

    /**
     * Replaces this observable array content with given elements. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param elements elements to put into array content
     */
    fun setAll(vararg elements: Short)

    /**
     * Replaces this observable array content with a copy of given array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src source observable array to copy.
     */
    fun setAll(src: Array<Short>) {
        setAll(*src.toShortArray())
    }

    /**
     * Replaces this observable array content with a copy of given observable array. Capacity is increased if necessary
     * to match the new size of the data.
     *
     * @param src source observable array to copy.
     */
    fun setAll(src: ObservableShortArray)

    /**
     * Replaces this observable array content with a copy of portion of a given array. Capacity is increased if
     * necessary to match the new size of the data.
     *
     * @param src source array to copy.
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: ShortArray, startIndex: Int, endIndex: Int)

    /**
     * Replaces this observable array content with a copy of portion of a given array. Capacity is increased if
     * necessary to match the new size of the data.
     *
     * @param src source array to copy.
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: Array<Short>, startIndex: Int, endIndex: Int) {
        setAll(src.toShortArray(), startIndex, endIndex)
    }

    /**
     * Replaces this observable array content with a copy of portion of a given observable array. Capacity is increased
     * if necessary to match the new size of the data.
     *
     * @param src source observable array to copy.
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: ObservableShortArray, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified array into this observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: ShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified array into this observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: Array<Short>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toShortArray(), destinationOffset, startIndex, endIndex)
    }

    /**
     * Copies a portion of specified observable array into this observable array. Throws the same exceptions as
     * [Array.copyInto] method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: ObservableShortArray, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Sets a single value in the array. Avoid using this method if many values are updated, use [set] update method
     * instead with as minimum number of invocations as possible.
     *
     * @param index index of the value to set
     * @param value new value for the given index
     *
     * @throws ArrayIndexOutOfBoundsException if `index` is outside array bounds
     */
    operator fun set(index: Int, value: Short)

    /**
     * Returns an array containing copy of the observable array.
     *
     * @return a short array containing the copy of the observable array
     */
    fun toShortArray(): ShortArray

    /**
     * Returns an array containing copy of specified portion of the observable array.
     *
     * @param startIndex starting position in this array
     * @param endIndex the end (exclusive) of the subrange to copy
     *
     * @return a short array containing the copy of specified portion the observable array
     */
    fun toShortArray(startIndex: Int, endIndex: Int): ShortArray

}