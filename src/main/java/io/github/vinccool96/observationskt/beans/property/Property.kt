package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.value.ObservableValue
import io.github.vinccool96.observationskt.beans.value.WritableValue

/**
 * Generic interface that defines the methods common to all (writable) properties independent of their type.
 *
 * @param T the type of the wrapped value
 */
interface Property<T> : ReadOnlyProperty<T>, WritableValue<T> {

    /**
     * Create a unidirectional binding for this `Property`.
     *
     * Note that JavaFX has all the bind calls implemented through weak listeners. This means the bound property can be
     * garbage collected and stopped from being updated.
     *
     * @param observable The observable this `Property` should be bound to.
     *
     * @see unbind
     */
    fun bind(observable: ObservableValue<out T>)

    /**
     * Remove the unidirectional binding for this `Property`.
     *
     * If the `Property` is not bound, calling this method has no effect.
     *
     * @see bind
     */
    fun unbind()

    /**
     * Can be used to check, if a `Property` is bound.
     *
     * @return `true` if the `Property` is bound, `false` otherwise
     *
     * @see bind
     * @see unbind
     * @see bindBidirectional
     * @see unbindBidirectional
     */
    val bound: Boolean

    /**
     * Create a bidirectional binding between this `Property` and another one. Bidirectional bindings exists
     * independently of unidirectional bindings. So it is possible to add unidirectional binding to a property with
     * bidirectional binding and vice-versa. However, this practice is discouraged.
     *
     * It is possible to have multiple bidirectional bindings of one Property.
     *
     * JavaFX bidirectional binding implementation use weak listeners. This means bidirectional binding does not prevent
     * properties from being garbage collected.
     *
     * @param other the other `Property`
     *
     * @throws IllegalArgumentException if `other` is `this`
     * @see unbindBidirectional
     */
    fun bindBidirectional(other: Property<T>)

    /**
     * Remove a bidirectional binding between this `Property` and another one.
     *
     * If no bidirectional binding between the properties exists, calling this method has no effect.
     *
     * It is possible to unbind by a call on the second property. This code will work:
     *
     * ```
     * property1.bindBidirectional(property2)
     * property2.unbindBidirectional(property1)
     * ```
     *
     * @param other the other `Property`
     *
     * @throws IllegalArgumentException if `other` is `this`
     * @see bindBidirectional
     */
    fun unbindBidirectional(other: Property<T>)

}