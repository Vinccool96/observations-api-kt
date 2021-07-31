package io.github.vinccool96.observationskt.collections

/**
 * `ObservableFloatArray` is a `Float` array that allows listeners to track changes when they occur. In order to track
 * changes, the internal array is encapsulated and there is no direct access available from the outside. Bulk operations
 * are supported but they always do a copy of the data range.
 *
 * @see ArrayChangeListener
 */
interface ObservableFloatArray : ObservableArray<ObservableFloatArray> {

    /**
     * Copies specified portion of array into `dest` array. Throws the same exceptions as [Array.copyInto] method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: FloatArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = this.size)

    /**
     * Copies specified portion of array into `dest` array. Throws the same exceptions as [Array.copyInto] method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: Array<Float>, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = this.size)

    /**
     * Copies specified portion of array into `dest` observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param destination destination array
     * @param destinationOffset starting position in [destination] array, 0 by default
     * @param startIndex starting position in the observable array, 0 by default
     * @param endIndex the end (exclusive) of the subrange to copy, size of this array by default.
     */
    fun copyInto(destination: ObservableFloatArray, destinationOffset: Int = 0, startIndex: Int = 0,
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
    operator fun get(index: Int): Float

    /**
     * Appends given `elements` to the end of this array. Capacity is increased if necessary to match the new size of
     * the data.
     *
     * @param elements elements to append
     */
    fun addAll(vararg elements: Float)

    /**
     * Appends content of a given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src array with elements to append
     */
    fun addAll(src: Array<Float>) {
        addAll(*src.toFloatArray())
    }

    /**
     * Appends content of a given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src observable array with elements to append
     */
    fun addAll(src: ObservableFloatArray)

    /**
     * Appends a portion of given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: FloatArray, startIndex: Int, endIndex: Int)

    /**
     * Appends a portion of given array to the end of this array. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: Array<Float>, startIndex: Int, endIndex: Int) {
        addAll(src.toFloatArray(), startIndex, endIndex)
    }

    /**
     * Appends a portion of given observable array to the end of this array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src source array
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to add
     */
    fun addAll(src: ObservableFloatArray, startIndex: Int, endIndex: Int)

    /**
     * Utility function that uses [addAll]
     */
    operator fun plusAssign(floats: FloatArray) {
        addAll(*floats)
    }

    /**
     * Utility function that uses [addAll]
     *
     * @param src array with elements to append
     */
    operator fun plusAssign(src: Array<Float>) {
        addAll(*src.toFloatArray())
    }

    /**
     * Utility function that uses [addAll]
     */
    operator fun plusAssign(src: ObservableFloatArray) {
        addAll(src)
    }

    /**
     * Replaces this observable array content with given elements. Capacity is increased if necessary to match the new
     * size of the data.
     *
     * @param elements elements to put into array content
     */
    fun setAll(vararg elements: Float)

    /**
     * Replaces this observable array content with a copy of given array. Capacity is increased if necessary to match
     * the new size of the data.
     *
     * @param src source observable array to copy.
     */
    fun setAll(src: Array<Float>) {
        setAll(*src.toFloatArray())
    }

    /**
     * Replaces this observable array content with a copy of given observable array. Capacity is increased if necessary
     * to match the new size of the data.
     *
     * @param src source observable array to copy.
     */
    fun setAll(src: ObservableFloatArray)

    /**
     * Replaces this observable array content with a copy of portion of a given array. Capacity is increased if
     * necessary to match the new size of the data.
     *
     * @param src source array to copy.
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: FloatArray, startIndex: Int, endIndex: Int)

    /**
     * Replaces this observable array content with a copy of portion of a given array. Capacity is increased if
     * necessary to match the new size of the data.
     *
     * @param src source array to copy.
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: Array<Float>, startIndex: Int, endIndex: Int) {
        setAll(src.toFloatArray(), startIndex, endIndex)
    }

    /**
     * Replaces this observable array content with a copy of portion of a given observable array. Capacity is increased
     * if necessary to match the new size of the data.
     *
     * @param src source observable array to copy.
     * @param startIndex starting position in source observable array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun setAll(src: ObservableFloatArray, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified array into this observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: FloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Copies a portion of specified array into this observable array. Throws the same exceptions as [Array.copyInto]
     * method.
     *
     * @param src source array to copy
     * @param destinationOffset the starting destination position in this observable array
     * @param startIndex starting position in source array
     * @param endIndex the end (exclusive) of the subrange to copy
     */
    fun set(src: Array<Float>, destinationOffset: Int, startIndex: Int, endIndex: Int) {
        set(src.toFloatArray(), destinationOffset, startIndex, endIndex)
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
    fun set(src: ObservableFloatArray, destinationOffset: Int, startIndex: Int, endIndex: Int)

    /**
     * Sets a single value in the array. Avoid using this method if many values are updated, use [set] update method
     * instead with as minimum number of invocations as possible.
     *
     * @param index index of the value to set
     * @param value new value for the given index
     *
     * @throws ArrayIndexOutOfBoundsException if `index` is outside array bounds
     */
    operator fun set(index: Int, value: Float)

    /**
     * Returns an array containing copy of the observable array.
     *
     * @return a float array containing the copy of the observable array
     */
    fun toFloatArray(): FloatArray

    /**
     * Returns an array containing copy of specified portion of the observable array.
     *
     * @param startIndex starting position in this array
     * @param endIndex the end (exclusive) of the subrange to copy
     *
     * @return a float array containing the copy of specified portion the observable array
     */
    fun toFloatArray(startIndex: Int, endIndex: Int): FloatArray

}