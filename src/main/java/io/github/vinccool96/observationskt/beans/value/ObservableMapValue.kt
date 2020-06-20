package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * An observable reference to an [ObservableMap].
 *
 * @param K
 *         the type of the key elements of the `Map`
 * @param V
 *         the type of the value elements of the `Map`
 *
 * @see ObservableMap
 * @see ObservableObjectValue
 * @see ObservableValue
 * @since JavaFX 2.1
 */
interface ObservableMapValue<K, V> : ObservableObjectValue<ObservableMap<K, V>>, ObservableMap<K, V>
