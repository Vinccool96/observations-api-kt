package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * An observable reference to an [ObservableSet].
 *
 * @param E
 *         the type of the `Set` elements
 *
 * @see ObservableSet
 * @see ObservableObjectValue
 * @see ObservableValue
 * @since JavaFX 2.1
 */
interface ObservableSetValue<E> : ObservableObjectValue<ObservableSet<E>>, ObservableSet<E>
