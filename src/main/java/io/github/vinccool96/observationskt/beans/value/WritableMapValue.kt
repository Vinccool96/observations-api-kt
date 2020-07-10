package io.github.vinccool96.observationskt.beans.value

import io.github.vinccool96.observationskt.collections.ObservableMap

/**
 * A writable reference to an [ObservableMap].
 *
 * @param K
 *         the type of the key elements of the `MutableMap`
 * @param V
 *         the type of the value elements of the `MutableMap`
 *
 * @see ObservableMap
 * @see WritableObjectValue
 */
interface WritableMapValue<K, V> : WritableObjectValue<ObservableMap<K, V>>, ObservableMap<K, V>
