package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * A writable reference to an [ObservableArray].
 *
 * @param E the type of the `Array` elements
 *
 * @see ObservableArray
 * @see WritableObjectValue
 * @see WritableValue
 */
interface WritableArrayValue<E> : WritableObjectValue<ObservableArray<E>?>, ObservableArray<E>