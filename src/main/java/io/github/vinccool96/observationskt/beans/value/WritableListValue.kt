package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * A writable reference to an [ObservableList].
 *
 * @param E the type of the `MutableList` elements
 *
 * @see ObservableList
 * @see WritableObjectValue
 * @see WritableValue
 */
interface WritableListValue<E> : WritableObjectValue<ObservableList<E>?>, ObservableList<E>
