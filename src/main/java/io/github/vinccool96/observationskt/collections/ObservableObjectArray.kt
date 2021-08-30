package io.github.vinccool96.observationskt.collections

/**
 * `ObservableObjectArray` is an array that allows listeners to track changes when they occur. In order to track
 * changes, the internal array is encapsulated and there is no direct access available from the outside. Bulk operations
 * are supported, but they always do a copy of the data range.
 *
 * @param T the type of the objects in the array
 *
 * @see ArrayChangeListener
 */
interface ObservableObjectArray<T> : ObservableArray<T> {

    /**
     * The base array that needs to be provided to fill the array when resizing. It must be of size `1`, and the value
     * it contains is the base value.
     */
    val baseArray: Array<T>

}