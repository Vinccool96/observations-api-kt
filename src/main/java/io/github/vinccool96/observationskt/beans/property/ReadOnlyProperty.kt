package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.value.ObservableValue

/**
 * Generic interface that defines the methods common to all readable properties independent of their type.
 *
 * @param T
 *         the type of the wrapped value
 *
 * @since JavaFX 2.0
 */
interface ReadOnlyProperty<T> : ObservableValue<T> {

    /**
     * Returns the `Object` that contains this property. If this property is not contained in an `Object`, `null` is
     * returned.
     *
     * @return the containing `Object` or `null`
     */
    val bean: Any?

    /**
     * Returns the name of this property. If the property does not have a name, this method returns an empty `String`.
     *
     * @return the name or an empty `String`
     */
    val name: String

}