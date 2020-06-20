package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * A writable reference to an [ObservableList].
 *
 * @param E
 *         the type of the `MutableList` elements
 *
 * @see ObservableList
 * @see WritableObjectValue
 * @see WritableListValue
 * @since JavaFX 2.1
 */
interface WritableListValue<E> : WritableObjectValue<ObservableList<E>>, ObservableList<E>
