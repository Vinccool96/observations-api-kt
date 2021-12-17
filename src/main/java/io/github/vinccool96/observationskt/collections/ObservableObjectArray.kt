package io.github.vinccool96.observationskt.collections

/**
 * `ObservableObjectArray` is an array that allows listeners to track changes when they occur. In order to track
 * changes, the internal array is encapsulated and there is no direct access available from the outside. Bulk operations
 * are supported, but they always do a copy of the data range.
 *
 * @param T the type of the `Array` elements
 *
 * @see ArrayChangeListener
 */
interface ObservableObjectArray<T> : ObservableArray<T>