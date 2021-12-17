package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * An observable reference to an [ObservableArray].
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see ObservableObjectValue
 * @see ObservableValue
 */
interface ObservableArrayValue<T> : ObservableObjectValue<ObservableArray<T>?>, ObservableArray<T>