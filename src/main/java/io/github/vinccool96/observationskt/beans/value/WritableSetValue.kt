package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * A writable reference to an [ObservableSet].
 *
 * @param E the type of the `MutableSet` elements
 *
 * @see ObservableSet
 * @see WritableObjectValue
 * @see WritableValue
 */
interface WritableSetValue<E> : WritableObjectValue<ObservableSet<E>?>, ObservableSet<E>
