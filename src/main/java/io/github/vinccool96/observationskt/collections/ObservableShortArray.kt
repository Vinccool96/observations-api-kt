package io.github.vinccool96.observationskt.collections

/**
 * `ObservableShortArray` is a `Short` array that allows listeners to track changes when they occur. In order to track
 * changes, the internal array is encapsulated and there is no direct access available from the outside. Bulk operations
 * are supported, but they always do a copy of the data range.
 *
 * @see ArrayChangeListener
 */
interface ObservableShortArray : ObservableArray<Short> {

    override val baseArray: Array<Short>
        get() = arrayOf(0)

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
     * Appends given `elements` to the end of this array. Capacity is increased if necessary to match the new size of
     * the data.
     *
     * @param elements elements to append
     */
    fun addAll(vararg elements: Short)

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
     * Utility function that uses [addAll]
     */
    operator fun plusAssign(shorts: ShortArray)

    /**
     * Replaces this observable array content with given elements. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param elements elements to put into array content
     */
    fun setAll(vararg elements: Short)

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