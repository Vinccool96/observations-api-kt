package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.ArrayExpression
import io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
import io.github.vinccool96.observationskt.collections.ObservableArray

/**
 * Super class for all readonly properties wrapping an [ObservableArray].
 *
 * @param T the type of the `Array` elements
 *
 * @see ObservableArray
 * @see io.github.vinccool96.observationskt.beans.value.ObservableArrayValue
 * @see ArrayExpression
 * @see ReadOnlyProperty
 *
 * @constructor The constructor needs a base array that [baseArray] will return when [ObservableArrayValue.get] returns
 * `null`. Therefore, `baseArray` will return the equivalent of `get()?.baseArray ?: baseArrayOfNull`.
 *
 * @param baseArrayOfNull the base array when the value is `null`
 */
abstract class ReadOnlyArrayProperty<T>(baseArrayOfNull: Array<T>) : ArrayExpression<T>(baseArrayOfNull),
        ReadOnlyProperty<ObservableArray<T>?> {

    /**
     * Creates a bidirectional content binding of the [ObservableArray], that is wrapped in this
     * `ReadOnlyArrayProperty`, and another `ObservableArray`.
     *
     * A bidirectional content binding ensures that the content of two `ObservableArrays` is the same. If the content of
     * one of the arrays changes, the other one will be updated automatically.
     *
     * @param array the `ObservableArray` this property should be bound to
     *
     * @throws IllegalArgumentException if `array` is the same array that this `ReadOnlyArrayProperty` points to
     */
    fun a() {
    }

}