package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableList

/**
 * An observable reference to an [ObservableList].
 *
 * @param E
 *         the type of the `List` elements
 *
 * @see ObservableList
 * @see ObservableObjectValue
 * @see ObservableValue
 * @since JavaFX 2.1
 */
interface ObservableListValue<E> : ObservableObjectValue<ObservableList<E>>, ObservableList<E>
