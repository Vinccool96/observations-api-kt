package io.github.vinccool96.observationskt.beans.property

import io.github.vinccool96.observationskt.beans.binding.ObjectExpression
import io.github.vinccool96.observationskt.collections.ObservableList
import io.github.vinccool96.observationskt.collections.ObservableMap
import io.github.vinccool96.observationskt.collections.ObservableSet

/**
 * Super class for all readonly properties wrapping an arbitrary `Object`.
 *
 * For specialized implementations for [ObservableList], [ObservableSet] and [ObservableMap] that also report changes
 * inside the collections, see [ReadOnlyListProperty], [ReadOnlySetProperty] and [ReadOnlyMapProperty], respectively.
 *
 * @param T the type of the wrapped `Object`
 *
 * @see io.github.vinccool96.observationskt.beans.value.ObservableObjectValue
 * @see ObjectExpression
 * @see ReadOnlyProperty
 *
 * @constructor The constructor of `ReadOnlyObjectProperty`.
 */
abstract class ReadOnlyObjectProperty<T> : ObjectExpression<T>(), ReadOnlyProperty<T> {

    /**
     * Returns a string representation of this `ReadOnlyObjectProperty` object.
     *
     * @return a string representation of this `ReadOnlyObjectProperty` object.
     */
    override fun toString(): String {
        val bean = this.bean
        val name = this.name
        val result = StringBuilder("ReadOnlyObjectProperty [")
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ")
        }
        if (name != null && name.isNotEmpty()) {
            result.append("name: ").append(name).append(", ")
        }
        result.append("value: ").append(get()).append("]")
        return result.toString()
    }

}